package com.gerenciadortarefas.auth.service;

import com.gerenciadortarefas.auth.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Orquestra todo o fluxo de autenticação. As classes externas (handlers)
 * só devem conversar com este serviço — nunca chamar repositórios direto.
 */
public class AuthService {

    public static final Duration SESSAO_TIMEOUT      = Duration.ofMinutes(30);
    public static final Duration MFA_CHALLENGE_TTL   = Duration.ofMinutes(5);

    private final UsuarioRepository usuarios;
    private final SessaoRepository  sessoes;
    private final MfaPendingRepository mfaPendings;
    private final PasswordHasher hasher;
    private final TokenService tokens;
    private final CryptoService crypto;
    private final TotpService totp;

    public AuthService(UsuarioRepository usuarios, SessaoRepository sessoes,
                       MfaPendingRepository mfaPendings, PasswordHasher hasher,
                       TokenService tokens, CryptoService crypto, TotpService totp) {
        this.usuarios = usuarios;
        this.sessoes = sessoes;
        this.mfaPendings = mfaPendings;
        this.hasher = hasher;
        this.tokens = tokens;
        this.crypto = crypto;
        this.totp = totp;
    }

    // ------------------------------------------------------------------
    // Cadastro
    // ------------------------------------------------------------------

    public Usuario registrar(String email, String username, String senhaPura) {
        validarEmail(email);
        validarUsername(username);
        validarForcaSenha(senhaPura);
        return usuarios.criar(email.toLowerCase(), username, hasher.hash(senhaPura));
    }

    private void validarEmail(String email) {
        if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$"))
            throw new IllegalArgumentException("E-mail inválido");
    }

    private void validarUsername(String username) {
        if (username == null || !username.matches("^[a-zA-Z0-9_.-]{3,60}$"))
            throw new IllegalArgumentException("Username deve ter 3-60 caracteres alfanuméricos");
    }

    private void validarForcaSenha(String senha) {
        if (senha == null || senha.length() < 8)
            throw new IllegalArgumentException("Senha deve ter ao menos 8 caracteres");
        if (!senha.matches(".*[A-Za-z].*") || !senha.matches(".*[0-9].*"))
            throw new IllegalArgumentException("Senha deve conter letras e números");
    }

    // ------------------------------------------------------------------
    // Login (passo 1: senha)
    // ------------------------------------------------------------------

    public sealed interface ResultadoLogin
            permits ResultadoLogin.Sucesso, ResultadoLogin.RequerMfa, ResultadoLogin.Falha {
        record Sucesso(String tokenSessao, String csrfToken, int usuarioId) implements ResultadoLogin {}
        record RequerMfa(String mfaToken) implements ResultadoLogin {}
        record Falha(String motivo) implements ResultadoLogin {}
    }

    public ResultadoLogin autenticar(String login, String senha, String ip, String userAgent) {
        Optional<Usuario> opt = usuarios.buscarPorLogin(login);
        if (opt.isEmpty() || !hasher.verificar(senha, opt.get().senhaHash())) {
            return new ResultadoLogin.Falha("Credenciais inválidas");
        }
        Usuario u = opt.get();
        if (u.mfaHabilitado()) {
            String mfaTokenPlano = tokens.gerarToken();
            mfaPendings.criar(new MfaPending(tokens.hash(mfaTokenPlano), u.id(),
                    LocalDateTime.now().plus(MFA_CHALLENGE_TTL)));
            return new ResultadoLogin.RequerMfa(mfaTokenPlano);
        }
        return criarSessaoLogada(u.id(), ip, userAgent);
    }

    // ------------------------------------------------------------------
    // Login (passo 2: TOTP)
    // ------------------------------------------------------------------

    public ResultadoLogin verificarMfa(String mfaTokenPlano, String codigo, String ip, String userAgent) {
        Optional<MfaPending> pend = mfaPendings.consumir(tokens.hash(mfaTokenPlano));
        if (pend.isEmpty()) return new ResultadoLogin.Falha("Challenge MFA inválido ou expirado");
        Usuario u = usuarios.buscarPorId(pend.get().usuarioId())
                .orElseThrow(() -> new IllegalStateException("Usuário sumiu"));
        if (!u.mfaHabilitado() || u.otpSecretCifrado() == null)
            return new ResultadoLogin.Falha("MFA não está configurado");
        String secret = crypto.decifrar(u.otpSecretCifrado());
        if (!totp.verificar(secret, codigo))
            return new ResultadoLogin.Falha("Código TOTP inválido");
        return criarSessaoLogada(u.id(), ip, userAgent);
    }

    private ResultadoLogin.Sucesso criarSessaoLogada(int usuarioId, String ip, String userAgent) {
        String tokenPlano = tokens.gerarToken();
        String csrf       = tokens.gerarToken();
        LocalDateTime agora = LocalDateTime.now();
        sessoes.criar(new Sessao(
                tokens.hash(tokenPlano), usuarioId, agora, agora,
                ip, userAgent, csrf, null, null));
        usuarios.atualizarUltimoLogin(usuarioId, agora);
        return new ResultadoLogin.Sucesso(tokenPlano, csrf, usuarioId);
    }

    // ------------------------------------------------------------------
    // Logout
    // ------------------------------------------------------------------

    public void logout(String tokenPlano) {
        if (tokenPlano == null) return;
        String hash = tokens.hash(tokenPlano);
        Optional<Sessao> s = sessoes.buscarPorTokenHash(hash);
        if (s.isPresent() && s.get().ativa()) {
            LocalDateTime agora = LocalDateTime.now();
            sessoes.encerrar(hash, "Logout manual feito pelo usuário", agora);
            usuarios.atualizarUltimoLogout(s.get().usuarioId(), agora);
        }
    }

    // ------------------------------------------------------------------
    // MFA setup / confirm
    // ------------------------------------------------------------------

    public record MfaSetup(String secretBase32, String qrCodeDataUri) {}

    public MfaSetup iniciarSetupMfa(int usuarioId) {
        Usuario u = usuarios.buscarPorId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário inexistente"));
        String secret = totp.novoSecret();
        // Persiste cifrado, mas mantém mfa_habilitado=false até a confirmação
        usuarios.definirSegredoMfa(usuarioId, crypto.cifrar(secret), false);
        return new MfaSetup(secret, totp.qrCodeDataUri(secret, u.email()));
    }

    public boolean confirmarSetupMfa(int usuarioId, String codigo) {
        Usuario u = usuarios.buscarPorId(usuarioId).orElseThrow();
        if (u.otpSecretCifrado() == null) return false;
        String secret = crypto.decifrar(u.otpSecretCifrado());
        if (!totp.verificar(secret, codigo)) return false;
        usuarios.definirSegredoMfa(usuarioId, u.otpSecretCifrado(), true);
        return true;
    }

    // ------------------------------------------------------------------
    // Acesso aos dependentes (usados pelo SessionFilter)
    // ------------------------------------------------------------------

    public TokenService tokenService()   { return tokens; }
    public SessaoRepository sessoes()    { return sessoes; }
    public UsuarioRepository usuarios()  { return usuarios; }
}

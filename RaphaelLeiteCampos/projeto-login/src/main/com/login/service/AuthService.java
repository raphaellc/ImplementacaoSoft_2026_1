package com.login.service;

import com.login.model.LoginRequest;
import com.login.model.LoginResponse;
import com.login.model.Usuario;
import com.login.repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.util.UUID;

/**
 * CAMADA SERVICE (Regras de Negócio)
 * ===================================
 * Aqui ficam TODAS as regras de negócio da autenticação.
 * O Service não sabe nada sobre HTTP — recebe e devolve objetos Java.
 * O Service não sabe nada sobre banco — delega ao Repository.
 *
 * Isso é o Princípio da Responsabilidade Única (SRP) em ação:
 *  - Repository → COMO persistir dados
 *  - Service    → O QUE fazer com os dados
 *  - Controller → COMO expor via HTTP
 *
 * BCrypt: algoritmo de hash moderno, com "salt" automático.
 * Nunca armazenamos a senha — apenas o hash irreversível.
 */
public class AuthService {

    // Injeção de dependência via construtor — facilita testes
    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Realiza o cadastro de um novo usuário.
     * Regras:
     *  1. Email não pode estar em uso
     *  2. Senha deve ter mínimo 6 caracteres
     *  3. A senha é hasheada antes de persistir
     */
    public LoginResponse cadastrar(String nome, String email, String senha) {
        // Validação básica
        if (nome == null || nome.isBlank()) {
            return LoginResponse.falha("Nome é obrigatório.");
        }
        if (email == null || !email.contains("@")) {
            return LoginResponse.falha("E-mail inválido.");
        }
        if (senha == null || senha.length() < 6) {
            return LoginResponse.falha("A senha deve ter no mínimo 6 caracteres.");
        }

        // Verifica duplicidade
        if (usuarioRepository.emailJaCadastrado(email)) {
            return LoginResponse.falha("Este e-mail já está cadastrado.");
        }

        // Gera hash seguro da senha (BCrypt inclui salt automaticamente)
        String senhaHash = BCrypt.hashpw(senha, BCrypt.gensalt(12));

        Usuario novoUsuario = new Usuario(nome.trim(), email.toLowerCase().trim(), senhaHash);
        usuarioRepository.salvar(novoUsuario);

        System.out.println("✓ Usuário cadastrado: " + novoUsuario);

        // Após cadastrar, já realiza o login
        return LoginResponse.sucesso(novoUsuario.getNome(), gerarToken());
    }

    /**
     * Autentica um usuário existente.
     * Regras:
     *  1. Email deve existir no banco
     *  2. A senha deve corresponder ao hash
     *  3. O usuário deve estar ativo
     *
     * IMPORTANTE: Mensagens de erro genéricas protegem contra
     * enumeração de usuários (não revelamos se o email existe).
     */
    public LoginResponse autenticar(LoginRequest request) {
        if (request.getEmail() == null || request.getSenha() == null) {
            return LoginResponse.falha("Informe email e senha.");
        }

        Optional<Usuario> encontrado = usuarioRepository.buscarPorEmail(request.getEmail());

        // Usuário não existe
        if (encontrado.isEmpty()) {
            return LoginResponse.falha("E-mail ou senha incorretos.");
        }

        Usuario usuario = encontrado.get();

        // Usuário inativo
        if (!usuario.isAtivo()) {
            return LoginResponse.falha("Conta desativada. Entre em contato com o suporte.");
        }

        // Verifica se a senha fornecida corresponde ao hash armazenado
        boolean senhaCorreta = BCrypt.checkpw(request.getSenha(), usuario.getSenhaHash());
        if (!senhaCorreta) {
            return LoginResponse.falha("E-mail ou senha incorretos.");
        }

        System.out.println("✓ Login bem-sucedido: " + usuario);
        return LoginResponse.sucesso(usuario.getNome(), gerarToken());
    }

    /**
     * Gera um token de sessão simples (UUID).
     * Em produção: usar JWT com expiração.
     */
    private String gerarToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

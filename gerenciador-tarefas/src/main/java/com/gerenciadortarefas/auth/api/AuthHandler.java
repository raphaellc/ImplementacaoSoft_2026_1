package com.gerenciadortarefas.auth.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gerenciadortarefas.auth.filter.SessionFilter;
import com.gerenciadortarefas.auth.model.Sessao;
import com.gerenciadortarefas.auth.model.Usuario;
import com.gerenciadortarefas.auth.service.AuthService;
import com.gerenciadortarefas.auth.service.AuthService.ResultadoLogin;
import com.gerenciadortarefas.auth.service.RateLimiter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Rotas:
 *   Públicas (contexto /auth):
 *     POST /auth/register           — body: { email, username, senha }
 *     POST /auth/login              — body: { login, senha }              → 200 Sucesso | 200 RequerMfa
 *     POST /auth/mfa/verify         — body: { mfaToken, codigo }
 *
 *   Autenticadas (contexto /auth/account, SessionFilter aplicado):
 *     POST /auth/account/logout
 *     POST /auth/account/mfa/setup
 *     POST /auth/account/mfa/confirm
 *     GET  /auth/account/me
 *     GET  /auth/account/csrf
 */
public class AuthHandler implements HttpHandler {

    private static final int MAX_BODY = 4096;
    private final AuthService auth;
    private final ObjectMapper json = new ObjectMapper();
    private final RateLimiter loginLimiter   = new RateLimiter(5, Duration.ofMinutes(15));
    private final RateLimiter mfaLimiter     = new RateLimiter(8, Duration.ofMinutes(15));

    public AuthHandler(AuthService auth) {
        this.auth = auth;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        headersBase(ex);
        if ("OPTIONS".equals(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); return; }

        String path = ex.getRequestURI().getPath();
        try {
            switch (path) {
                case "/auth/register"           -> registrar(ex);
                case "/auth/login"              -> login(ex);
                case "/auth/mfa/verify"         -> mfaVerify(ex);
                case "/auth/account/logout"     -> logout(ex);
                case "/auth/account/mfa/setup"  -> mfaSetup(ex);
                case "/auth/account/mfa/confirm"-> mfaConfirm(ex);
                case "/auth/account/me"         -> me(ex);
                case "/auth/account/csrf"       -> csrf(ex);
                default -> responder(ex, 404, "{\"mensagem\":\"Rota não encontrada\"}");
            }
        } catch (IllegalArgumentException iae) {
            responder(ex, 400, msg(iae.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            responder(ex, 500, "{\"mensagem\":\"Erro interno\"}");
        }
    }

    // ----- Rotas -----

    private void registrar(HttpExchange ex) throws IOException {
        exigirMetodo(ex, "POST");
        JsonNode b = lerJson(ex);
        Usuario u = auth.registrar(
                texto(b, "email"), texto(b, "username"), texto(b, "senha"));
        ObjectNode resp = json.createObjectNode();
        resp.put("id", u.id());
        resp.put("email", u.email());
        resp.put("username", u.username());
        responder(ex, 201, resp.toString());
    }

    private void login(HttpExchange ex) throws IOException {
        exigirMetodo(ex, "POST");
        String ip = ipDoCliente(ex);
        if (!loginLimiter.permitir(ip)) {
            responder(ex, 429, "{\"mensagem\":\"Muitas tentativas — aguarde 15 minutos\"}");
            return;
        }
        JsonNode b = lerJson(ex);
        String login = texto(b, "login");
        String senha = texto(b, "senha");
        String ua    = ex.getRequestHeaders().getFirst("User-Agent");

        ResultadoLogin r = auth.autenticar(login, senha, ip, ua);
        switch (r) {
            case ResultadoLogin.Sucesso s -> {
                loginLimiter.limpar(ip);
                gravarCookieSessao(ex, s.tokenSessao());
                ObjectNode resp = json.createObjectNode();
                resp.put("status", "ok");
                resp.put("csrfToken", s.csrfToken());
                resp.put("usuarioId", s.usuarioId());
                responder(ex, 200, resp.toString());
            }
            case ResultadoLogin.RequerMfa m -> {
                ObjectNode resp = json.createObjectNode();
                resp.put("status", "mfa_requerido");
                resp.put("mfaToken", m.mfaToken());
                responder(ex, 200, resp.toString());
            }
            case ResultadoLogin.Falha f -> responder(ex, 401, msg(f.motivo()));
        }
    }

    private void mfaVerify(HttpExchange ex) throws IOException {
        exigirMetodo(ex, "POST");
        String ip = ipDoCliente(ex);
        if (!mfaLimiter.permitir(ip)) {
            responder(ex, 429, "{\"mensagem\":\"Muitas tentativas de MFA\"}");
            return;
        }
        JsonNode b = lerJson(ex);
        ResultadoLogin r = auth.verificarMfa(
                texto(b, "mfaToken"), texto(b, "codigo"),
                ip, ex.getRequestHeaders().getFirst("User-Agent"));
        switch (r) {
            case ResultadoLogin.Sucesso s -> {
                mfaLimiter.limpar(ip);
                gravarCookieSessao(ex, s.tokenSessao());
                ObjectNode resp = json.createObjectNode();
                resp.put("status", "ok");
                resp.put("csrfToken", s.csrfToken());
                resp.put("usuarioId", s.usuarioId());
                responder(ex, 200, resp.toString());
            }
            case ResultadoLogin.Falha f    -> responder(ex, 401, msg(f.motivo()));
            case ResultadoLogin.RequerMfa ignored ->
                    responder(ex, 500, "{\"mensagem\":\"Estado inesperado\"}");
        }
    }

    private void logout(HttpExchange ex) throws IOException {
        exigirMetodo(ex, "POST");
        String token = SessionFilter.lerCookie(ex, "sessao");
        auth.logout(token);
        // Limpa cookie
        ex.getResponseHeaders().add("Set-Cookie",
                "sessao=; Path=/; Max-Age=0; HttpOnly; SameSite=Strict");
        responder(ex, 200, "{\"status\":\"ok\"}");
    }

    private void mfaSetup(HttpExchange ex) throws IOException {
        exigirMetodo(ex, "POST");
        Integer userId = (Integer) ex.getAttribute("usuarioId");
        if (userId == null) { responder(ex, 401, msg("Não autenticado")); return; }
        var setup = auth.iniciarSetupMfa(userId);
        ObjectNode resp = json.createObjectNode();
        resp.put("secret", setup.secretBase32());
        resp.put("qrCode", setup.qrCodeDataUri());
        responder(ex, 200, resp.toString());
    }

    private void mfaConfirm(HttpExchange ex) throws IOException {
        exigirMetodo(ex, "POST");
        Integer userId = (Integer) ex.getAttribute("usuarioId");
        if (userId == null) { responder(ex, 401, msg("Não autenticado")); return; }
        JsonNode b = lerJson(ex);
        boolean ok = auth.confirmarSetupMfa(userId, texto(b, "codigo"));
        if (!ok) { responder(ex, 400, msg("Código inválido")); return; }
        responder(ex, 200, "{\"status\":\"mfa_habilitado\"}");
    }

    private void me(HttpExchange ex) throws IOException {
        exigirMetodo(ex, "GET");
        Integer userId = (Integer) ex.getAttribute("usuarioId");
        if (userId == null) { responder(ex, 401, msg("Não autenticado")); return; }
        Usuario u = auth.usuarios().buscarPorId(userId).orElseThrow();
        ObjectNode resp = json.createObjectNode();
        resp.put("id", u.id());
        resp.put("email", u.email());
        resp.put("username", u.username());
        resp.put("mfaHabilitado", u.mfaHabilitado());
        responder(ex, 200, resp.toString());
    }

    private void csrf(HttpExchange ex) throws IOException {
        exigirMetodo(ex, "GET");
        Sessao s = (Sessao) ex.getAttribute("sessao");
        if (s == null) { responder(ex, 401, msg("Não autenticado")); return; }
        responder(ex, 200, "{\"csrfToken\":\"" + s.csrfToken() + "\"}");
    }

    // ----- Utilidades -----

    private void exigirMetodo(HttpExchange ex, String m) {
        if (!m.equals(ex.getRequestMethod()))
            throw new IllegalArgumentException("Método não permitido");
    }

    private void gravarCookieSessao(HttpExchange ex, String tokenPlano) {
        long maxAge = AuthService.SESSAO_TIMEOUT.toSeconds();
        // Em produção HTTPS adicionar "Secure;"
        ex.getResponseHeaders().add("Set-Cookie",
                "sessao=" + tokenPlano + "; Path=/; Max-Age=" + maxAge +
                "; HttpOnly; SameSite=Strict");
    }

    private void headersBase(HttpExchange ex) {
        ex.getResponseHeaders().add("X-Content-Type-Options", "nosniff");
        ex.getResponseHeaders().add("X-Frame-Options", "DENY");
        ex.getResponseHeaders().add("Cache-Control", "no-store");
    }

    private String ipDoCliente(HttpExchange ex) {
        String xf = ex.getRequestHeaders().getFirst("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) return xf.split(",")[0].trim();
        return ex.getRemoteAddress().getAddress().getHostAddress();
    }

    private JsonNode lerJson(HttpExchange ex) throws IOException {
        try (InputStream is = ex.getRequestBody();
             ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
            byte[] chunk = new byte[512];
            int n, total = 0;
            while ((n = is.read(chunk)) != -1) {
                total += n;
                if (total > MAX_BODY) throw new IllegalArgumentException("Body muito grande");
                buf.write(chunk, 0, n);
            }
            return json.readTree(buf.toString(StandardCharsets.UTF_8));
        }
    }

    private String texto(JsonNode n, String campo) {
        JsonNode v = n.get(campo);
        if (v == null || v.isNull()) throw new IllegalArgumentException("Campo obrigatório: " + campo);
        return v.asText();
    }

    private String msg(String s) { return "{\"mensagem\":\"" + s.replace("\"", "\\\"") + "\"}"; }

    private void responder(HttpExchange ex, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }
}

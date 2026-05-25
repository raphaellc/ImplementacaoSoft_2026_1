package com.gerenciadortarefas.auth.filter;

import com.gerenciadortarefas.auth.model.Sessao;
import com.gerenciadortarefas.auth.service.AuthService;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Valida o cookie {@code sessao}, aplica timeout de inatividade e expõe
 * {@code usuarioId} e {@code sessao} via {@code exchange.setAttribute(...)}.
 *
 * <p>Otimização: o {@code last_activity} só é gravado se mais de 60s
 * passaram desde o último update — evita 1 write por request em SPAs ativos.
 */
public class SessionFilter extends Filter {

    private static final String COOKIE_NAME = "sessao";
    private static final long UPDATE_THROTTLE_SECONDS = 60;

    private final AuthService auth;

    public SessionFilter(AuthService auth) {
        this.auth = auth;
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        String token = lerCookie(exchange, COOKIE_NAME);
        if (token == null) {
            responder401(exchange, "Sem sessão");
            return;
        }

        String tokenHash = auth.tokenService().hash(token);
        Optional<Sessao> opt = auth.sessoes().buscarPorTokenHash(tokenHash);
        if (opt.isEmpty() || !opt.get().ativa()) {
            responder401(exchange, "Sessão inválida");
            return;
        }

        Sessao s = opt.get();
        LocalDateTime agora = LocalDateTime.now();
        long segInativo = ChronoUnit.SECONDS.between(s.lastActivity(), agora);

        if (segInativo > AuthService.SESSAO_TIMEOUT.toSeconds()) {
            auth.sessoes().encerrar(tokenHash, "Timeout de sessão", agora);
            auth.usuarios().atualizarUltimoLogout(s.usuarioId(), agora);
            responder401(exchange, "Sessão expirada");
            return;
        }

        if (segInativo > UPDATE_THROTTLE_SECONDS) {
            auth.sessoes().atualizarAtividade(tokenHash, agora);
        }

        exchange.setAttribute("usuarioId", s.usuarioId());
        exchange.setAttribute("sessao", s);
        chain.doFilter(exchange);
    }

    @Override
    public String description() {
        return "Valida sessão (cookie HttpOnly) e aplica timeout de 30 min";
    }

    public static String lerCookie(HttpExchange ex, String nome) {
        var lista = ex.getRequestHeaders().get("Cookie");
        if (lista == null) return null;
        for (String header : lista) {
            for (String par : header.split(";")) {
                String[] kv = par.trim().split("=", 2);
                if (kv.length == 2 && kv[0].equals(nome)) return kv[1];
            }
        }
        return null;
    }

    private void responder401(HttpExchange ex, String motivo) throws IOException {
        byte[] body = ("{\"mensagem\":\"" + motivo + "\"}").getBytes();
        ex.getResponseHeaders().set("Content-Type", "application/json");
        ex.sendResponseHeaders(401, body.length);
        try (var os = ex.getResponseBody()) { os.write(body); }
    }
}

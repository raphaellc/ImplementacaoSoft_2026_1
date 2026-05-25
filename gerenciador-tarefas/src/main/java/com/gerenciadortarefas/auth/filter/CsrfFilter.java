package com.gerenciadortarefas.auth.filter;

import com.gerenciadortarefas.auth.model.Sessao;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Set;

/**
 * Aplica-se DEPOIS do SessionFilter — espera encontrar a sessão como atributo.
 * Para métodos mutadores (POST/PUT/DELETE/PATCH), exige o header
 * {@code X-CSRF-Token} com valor igual ao {@code csrfToken} da sessão.
 *
 * <p>Defesa contra CSRF mesmo com SameSite=Lax (pull-request: usar Strict).
 */
public class CsrfFilter extends Filter {

    private static final Set<String> METODOS_MUTADORES = Set.of("POST", "PUT", "DELETE", "PATCH");

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        String metodo = exchange.getRequestMethod();
        if (METODOS_MUTADORES.contains(metodo)) {
            Sessao s = (Sessao) exchange.getAttribute("sessao");
            String headerCsrf = exchange.getRequestHeaders().getFirst("X-CSRF-Token");
            if (s == null || headerCsrf == null || !s.csrfToken().equals(headerCsrf)) {
                byte[] body = "{\"mensagem\":\"CSRF token ausente ou inválido\"}".getBytes();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(403, body.length);
                try (var os = exchange.getResponseBody()) { os.write(body); }
                return;
            }
        }
        chain.doFilter(exchange);
    }

    @Override
    public String description() {
        return "Valida header X-CSRF-Token contra a sessão";
    }
}

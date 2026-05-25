package com.gerenciadortarefas.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gerenciadortarefas.service.TarefaService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * CRUD de tarefas. Espera que o SessionFilter já tenha colocado o atributo
 * {@code usuarioId} no exchange — caso contrário responde 401.
 */
public class TarefaHandler implements HttpHandler {

    private static final int MAX_BODY_BYTES = 1024;
    private static final String ALLOWED_ORIGIN =
            System.getenv().getOrDefault("CORS_ORIGIN", "http://localhost:8080");

    private final TarefaService service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TarefaHandler(TarefaService service) {
        this.service = service;
    }

    private void addSecurityHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("X-Content-Type-Options", "nosniff");
        exchange.getResponseHeaders().add("X-Frame-Options", "DENY");
        exchange.getResponseHeaders().add("Content-Security-Policy", "default-src 'self'");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, X-CSRF-Token");
        exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
        addSecurityHeaders(exchange);

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        Integer usuarioId = (Integer) exchange.getAttribute("usuarioId");
        if (usuarioId == null) {
            sendResponse(exchange, 401, "Não autenticado");
            return;
        }

        if ("GET".equals(method))         handleGet(exchange, usuarioId);
        else if ("POST".equals(method))   handlePost(exchange, usuarioId);
        else if ("PUT".equals(method))    handlePut(exchange, usuarioId, path);
        else if ("DELETE".equals(method)) handleDelete(exchange, usuarioId, path);
        else                              sendResponse(exchange, 405, "Método não permitido");
    }

    private void handleGet(HttpExchange exchange, int usuarioId) throws IOException {
        var tarefas = service.listarTarefas(usuarioId);
        byte[] response = objectMapper.writeValueAsBytes(tarefas);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(response); }
    }

    private void handlePost(HttpExchange exchange, int usuarioId) throws IOException {
        String body = readBody(exchange);
        if (body == null) { sendResponse(exchange, 400, "Corpo da requisição muito grande ou ausente"); return; }

        String descricao;
        try {
            JsonNode node = objectMapper.readTree(body);
            JsonNode campo = node.get("descricao");
            if (campo == null || campo.isNull()) { sendResponse(exchange, 400, "Campo 'descricao' é obrigatório"); return; }
            descricao = campo.asText().trim();
        } catch (Exception e) { sendResponse(exchange, 400, "JSON inválido"); return; }

        if (descricao.isBlank())       { sendResponse(exchange, 400, "A descrição não pode ser vazia"); return; }
        if (descricao.length() > 255)  { sendResponse(exchange, 400, "A descrição deve ter no máximo 255 caracteres"); return; }

        try {
            var tarefaCriada = service.adicionarTarefa(usuarioId, descricao);
            byte[] response = objectMapper.writeValueAsBytes(tarefaCriada);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(201, response.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(response); }
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, e.getMessage());
        } catch (Exception e) {
            sendResponse(exchange, 500, "Erro interno ao criar tarefa");
        }
    }

    private void handlePut(HttpExchange exchange, int usuarioId, String path) throws IOException {
        Integer id = extrairId(exchange, path, "PUT");
        if (id == null) return;

        String body = readBody(exchange);
        if (body == null) { sendResponse(exchange, 400, "Corpo da requisição muito grande ou ausente"); return; }

        String descricao = null;
        Boolean concluida = null;
        try {
            JsonNode node = objectMapper.readTree(body);
            JsonNode cd = node.get("descricao");
            JsonNode cc = node.get("concluida");
            if (cd != null && !cd.isNull()) {
                descricao = cd.asText().trim();
                if (descricao.isBlank())      { sendResponse(exchange, 400, "A descrição não pode ser vazia"); return; }
                if (descricao.length() > 255) { sendResponse(exchange, 400, "A descrição deve ter no máximo 255 caracteres"); return; }
            }
            if (cc != null && !cc.isNull()) concluida = cc.asBoolean();
        } catch (Exception e) { sendResponse(exchange, 400, "JSON inválido"); return; }

        if (descricao == null && concluida == null) {
            sendResponse(exchange, 400, "Informe ao menos 'descricao' ou 'concluida' para atualizar");
            return;
        }

        try {
            var tarefa = service.atualizarTarefa(usuarioId, id, descricao, concluida);
            if (tarefa.isEmpty()) { sendResponse(exchange, 404, "Tarefa com ID " + id + " não encontrada"); return; }
            byte[] response = objectMapper.writeValueAsBytes(tarefa.get());
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(response); }
        } catch (Exception e) {
            sendResponse(exchange, 500, "Erro interno ao atualizar tarefa");
        }
    }

    private void handleDelete(HttpExchange exchange, int usuarioId, String path) throws IOException {
        Integer id = extrairId(exchange, path, "DELETE");
        if (id == null) return;
        try {
            boolean removida = service.deletarTarefa(usuarioId, id);
            if (!removida) { sendResponse(exchange, 404, "Tarefa com ID " + id + " não encontrada"); return; }
            sendResponse(exchange, 200, "Tarefa com ID " + id + " removida com sucesso");
        } catch (Exception e) {
            sendResponse(exchange, 500, "Erro interno ao deletar tarefa");
        }
    }

    private Integer extrairId(HttpExchange ex, String path, String metodo) throws IOException {
        String[] parts = path.split("/");
        if (parts.length < 4) { sendResponse(ex, 400, "Informe o ID: " + metodo + " /api/tarefas/{id}"); return null; }
        try { return Integer.parseInt(parts[parts.length - 1]); }
        catch (NumberFormatException e) { sendResponse(ex, 400, "ID inválido"); return null; }
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] chunk = new byte[512];
            int bytesRead, totalRead = 0;
            while ((bytesRead = is.read(chunk)) != -1) {
                totalRead += bytesRead;
                if (totalRead > MAX_BODY_BYTES) return null;
                buffer.write(chunk, 0, bytesRead);
            }
            return buffer.toString(StandardCharsets.UTF_8);
        }
    }

    private void sendResponse(HttpExchange exchange, int status, String message) throws IOException {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("mensagem", message);
        byte[] body = objectMapper.writeValueAsBytes(node);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, body.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(body); }
    }
}

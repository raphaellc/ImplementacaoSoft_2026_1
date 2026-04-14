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

public class TarefaHandler implements HttpHandler {

    private static final int MAX_BODY_BYTES = 1024; // 1 KB
    private static final String ALLOWED_ORIGIN =
            System.getenv().getOrDefault("CORS_ORIGIN", "http://localhost:8080");

    private final TarefaService service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TarefaHandler(TarefaService service) {
        this.service = service;
    }

    // Falha 3 corrigida: adiciona headers de segurança em todas as respostas
    private void addSecurityHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("X-Content-Type-Options", "nosniff");
        exchange.getResponseHeaders().add("X-Frame-Options", "DENY");
        exchange.getResponseHeaders().add("Content-Security-Policy", "default-src 'self'");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        addSecurityHeaders(exchange);

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath(); // ex: /api/tarefas ou /api/tarefas/3

        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("GET".equals(method)) {
            handleGet(exchange);
        } else if ("POST".equals(method)) {
            handlePost(exchange);
        } else if ("PUT".equals(method)) {
            handlePut(exchange, path);
        } else {
            sendResponse(exchange, 405, "Método não permitido");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        var tarefas = service.listarTarefas();
        byte[] response = objectMapper.writeValueAsBytes(tarefas);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        if (body == null) {
            sendResponse(exchange, 400, "Corpo da requisição muito grande ou ausente");
            return;
        }

        String descricao;
        try {
            JsonNode node = objectMapper.readTree(body);
            JsonNode campo = node.get("descricao");
            if (campo == null || campo.isNull()) {
                sendResponse(exchange, 400, "Campo 'descricao' é obrigatório");
                return;
            }
            descricao = campo.asText().trim();
        } catch (Exception e) {
            sendResponse(exchange, 400, "JSON inválido");
            return;
        }

        if (descricao.isBlank()) {
            sendResponse(exchange, 400, "A descrição não pode ser vazia");
            return;
        }
        if (descricao.length() > 255) {
            sendResponse(exchange, 400, "A descrição deve ter no máximo 255 caracteres");
            return;
        }

        try {
            var tarefaCriada = service.adicionarTarefa(descricao);
            byte[] response = objectMapper.writeValueAsBytes(tarefaCriada);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(201, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, e.getMessage());
        } catch (Exception e) {
            sendResponse(exchange, 500, "Erro interno ao criar tarefa");
        }
    }

    private void handlePut(HttpExchange exchange, String path) throws IOException {
        // Espera: PUT /api/tarefas/{id}
        String[] parts = path.split("/");
        if (parts.length < 4) {
            sendResponse(exchange, 400, "Informe o ID da tarefa: PUT /api/tarefas/{id}");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(parts[parts.length - 1]);
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "ID inválido");
            return;
        }

        try {
            var tarefaAtualizada = service.marcarTarefaConcluida(id);
            if (tarefaAtualizada.isEmpty()) {
                sendResponse(exchange, 404, "Tarefa com ID " + id + " não encontrada");
                return;
            }
            byte[] response = objectMapper.writeValueAsBytes(tarefaAtualizada.get());
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "Erro interno ao atualizar tarefa");
        }
    }

    // Falha 2 corrigida: lê em loop para garantir que o limite seja sempre respeitado,
    // independentemente de fragmentação TCP.
    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] chunk = new byte[512];
            int bytesRead;
            int totalRead = 0;
            while ((bytesRead = is.read(chunk)) != -1) {
                totalRead += bytesRead;
                if (totalRead > MAX_BODY_BYTES) {
                    return null; // payload muito grande
                }
                buffer.write(chunk, 0, bytesRead);
            }
            return buffer.toString(StandardCharsets.UTF_8);
        }
    }

    // Falha 1 corrigida: usa ObjectMapper para serializar a mensagem com escape correto,
    // eliminando o risco de injeção de JSON por caracteres especiais (aspas, barras, etc).
    private void sendResponse(HttpExchange exchange, int status, String message) throws IOException {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("mensagem", message);
        byte[] body = objectMapper.writeValueAsBytes(node);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, body.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body);
        }
    }
}

package com.gerenciadortarefas.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerenciadortarefas.service.TarefaService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class TarefaHandler implements HttpHandler {

    private static final int MAX_BODY_BYTES = 1024; // 1 KB
    private static final String ALLOWED_ORIGIN =
            System.getenv().getOrDefault("CORS_ORIGIN", "http://localhost:3000");

    private final TarefaService service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TarefaHandler(TarefaService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

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
            service.adicionarTarefa(descricao);
            sendResponse(exchange, 201, "Tarefa criada com sucesso");
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
            String resultado = service.marcarTarefaConcluida(id);
            if (resultado.contains("não encontrada")) {
                sendResponse(exchange, 404, resultado);
            } else if (resultado.contains("Erro")) {
                sendResponse(exchange, 500, "Erro interno ao atualizar tarefa");
            } else {
                sendResponse(exchange, 200, resultado);
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "Erro interno ao atualizar tarefa");
        }
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            byte[] buffer = new byte[MAX_BODY_BYTES + 1];
            int bytesRead = is.read(buffer);
            if (bytesRead > MAX_BODY_BYTES) {
                return null; // payload muito grande
            }
            if (bytesRead <= 0) {
                return "";
            }
            return new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
        }
    }

    private void sendResponse(HttpExchange exchange, int status, String message) throws IOException {
        byte[] body = ("{\"mensagem\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, body.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body);
        }
    }
}

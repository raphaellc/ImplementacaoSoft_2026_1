package br.edu.projeto.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import br.edu.projeto.service.LivroService;

public class LivroHandler implements HttpHandler {

    private static final int MAX_BODY_BYTES = 1024; 
    private static final String ALLOWED_ORIGIN = 
            System.getenv().getOrDefault("CORS_ORIGIN", "http://localhost:8080");

    private final LivroService service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LivroHandler(LivroService service) {
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
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        addSecurityHeaders(exchange);

        String method = exchange.getRequestMethod();

        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("GET".equals(method)) {
            handleGet(exchange);
        } else if ("POST".equals(method)) {
            handlePost(exchange);
        } else {
            sendResponse(exchange, 405, "Método não permitido");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        try {
            var livros = service.listarLivros();
            byte[] response = objectMapper.writeValueAsBytes(livros);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "Erro ao listar livros");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        if (body == null) {
            sendResponse(exchange, 400, "Corpo da requisição muito grande ou ausente");
            return;
        }

        try {
            JsonNode node = objectMapper.readTree(body);
            
            if (!node.has("titulo") || !node.has("autor")) {
                sendResponse(exchange, 400, "Campos 'titulo' e 'autor' são obrigatórios");
                return;
            }

            String titulo = node.get("titulo").asText().trim();
            String autor = node.get("autor").asText().trim();

            if (titulo.isBlank() || autor.isBlank()) {
                sendResponse(exchange, 400, "Título e Autor não podem ser vazios");
                return;
            }

            service.adicionarLivro(titulo, autor);
            sendResponse(exchange, 201, "Livro adicionado com sucesso!");

        } catch (Exception e) {
            sendResponse(exchange, 400, "JSON inválido");
        }
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] chunk = new byte[512];
            int bytesRead;
            int totalRead = 0;
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
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body);
        }
    }
}
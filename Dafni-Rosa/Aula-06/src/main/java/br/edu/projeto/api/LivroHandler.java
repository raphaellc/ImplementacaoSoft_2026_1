package br.edu.projeto.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import br.edu.projeto.service.LivroService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class LivroHandler implements HttpHandler {

    private static final int MAX_BODY_BYTES = 2048;
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
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        addSecurityHeaders(exchange);

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

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
        var livros = service.listarLivros();
        byte[] response = objectMapper.writeValueAsBytes(livros);
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

        String titulo;
        String autor;
        try {
            JsonNode node = objectMapper.readTree(body);
            JsonNode campoTitulo = node.get("titulo");
            JsonNode campoAutor = node.get("autor");

            if (campoTitulo == null || campoTitulo.isNull()) {
                sendResponse(exchange, 400, "Campo 'titulo' é obrigatório");
                return;
            }
            if (campoAutor == null || campoAutor.isNull()) {
                sendResponse(exchange, 400, "Campo 'autor' é obrigatório");
                return;
            }

            titulo = campoTitulo.asText().trim();
            autor = campoAutor.asText().trim();
        } catch (Exception e) {
            sendResponse(exchange, 400, "JSON inválido");
            return;
        }

        try {
            var livroCriado = service.adicionarLivro(titulo, autor);
            byte[] response = objectMapper.writeValueAsBytes(livroCriado);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(201, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, e.getMessage());
        } catch (Exception e) {
            sendResponse(exchange, 500, "Erro interno ao criar livro");
        }
    }

    private void handlePut(HttpExchange exchange, String path) throws IOException {
        // Espera: PUT /api/livros/{id}
        String[] parts = path.split("/");
        if (parts.length < 4) {
            sendResponse(exchange, 400, "Informe o ID do livro: PUT /api/livros/{id}");
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
            var livroAtualizado = service.marcarLivroComoLido(id);
            if (livroAtualizado.isEmpty()) {
                sendResponse(exchange, 404, "Livro com ID " + id + " não encontrado");
                return;
            }
            byte[] response = objectMapper.writeValueAsBytes(livroAtualizado.get());
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "Erro interno ao atualizar livro");
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
                if (totalRead > MAX_BODY_BYTES) {
                    return null;
                }
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
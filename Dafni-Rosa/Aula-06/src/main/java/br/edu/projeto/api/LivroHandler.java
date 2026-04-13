package br.edu.projeto.api;

import br.edu.projeto.service.LivroService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Handler HTTP que expõe as operações do LivroService como API REST.
 * Espelha a estrutura de TarefaHandler.
 *
 * Rotas:
 *   GET    /api/livros          → listar todos
 *   POST   /api/livros          → adicionar
 *   PUT    /api/livros/{id}     → atualizar livro completo
 *   PATCH  /api/livros/{id}     → alterar disponibilidade
 *   DELETE /api/livros/{id}     → remover
 */
public class LivroHandler implements HttpHandler {

    private static final int MAX_BODY_BYTES = 4096; // 4 KB
    private static final String ALLOWED_ORIGIN =
            System.getenv().getOrDefault("CORS_ORIGIN", "http://localhost:3000");

    private final LivroService service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LivroHandler(LivroService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Cabeçalhos CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        String method = exchange.getRequestMethod();
        String path   = exchange.getRequestURI().getPath(); // ex: /api/livros ou /api/livros/3

        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        switch (method) {
            case "GET"    -> handleGet(exchange);
            case "POST"   -> handlePost(exchange);
            case "PUT"    -> handlePut(exchange, path);
            case "PATCH"  -> handlePatch(exchange, path);
            case "DELETE" -> handleDelete(exchange, path);
            default       -> sendResponse(exchange, 405, "Método não permitido");
        }
    }

    // -----------------------------------------------------------------------
    // GET /api/livros → lista todos os livros
    // -----------------------------------------------------------------------
    private void handleGet(HttpExchange exchange) throws IOException {
        var livros = service.listarLivros();
        byte[] response = objectMapper.writeValueAsBytes(livros);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    // -----------------------------------------------------------------------
    // POST /api/livros → { "titulo": "...", "autor": "...", "isbn": "..." }
    // -----------------------------------------------------------------------
    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        if (body == null) {
            sendResponse(exchange, 400, "Corpo da requisição muito grande ou ausente");
            return;
        }

        String titulo, autor, isbn;
        try {
            JsonNode node = objectMapper.readTree(body);

            JsonNode campoTitulo = node.get("titulo");
            JsonNode campoAutor  = node.get("autor");
            JsonNode campoIsbn   = node.get("isbn");

            if (campoTitulo == null || campoTitulo.isNull()) {
                sendResponse(exchange, 400, "Campo 'titulo' é obrigatório");
                return;
            }
            if (campoAutor == null || campoAutor.isNull()) {
                sendResponse(exchange, 400, "Campo 'autor' é obrigatório");
                return;
            }

            titulo = campoTitulo.asText().trim();
            autor  = campoAutor.asText().trim();
            isbn   = (campoIsbn != null && !campoIsbn.isNull()) ? campoIsbn.asText().trim() : "";

        } catch (Exception e) {
            sendResponse(exchange, 400, "JSON inválido");
            return;
        }

        try {
            int idGerado = service.adicionarLivro(titulo, autor, isbn);
            if (idGerado > 0) {
                sendResponse(exchange, 201, "Livro criado com sucesso. ID: " + idGerado);
            } else {
                sendResponse(exchange, 500, "Erro interno ao criar livro");
            }
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, e.getMessage());
        } catch (Exception e) {
            sendResponse(exchange, 500, "Erro interno ao criar livro");
        }
    }

    // -----------------------------------------------------------------------
    // PUT /api/livros/{id} → atualização completa
    // { "titulo": "...", "autor": "...", "isbn": "...", "disponivel": true }
    // -----------------------------------------------------------------------
    private void handlePut(HttpExchange exchange, String path) throws IOException {
        int id = extrairId(exchange, path);
        if (id < 0) return;

        String body = readBody(exchange);
        if (body == null) {
            sendResponse(exchange, 400, "Corpo da requisição muito grande ou ausente");
            return;
        }

        String titulo, autor, isbn;
        boolean disponivel;
        try {
            JsonNode node = objectMapper.readTree(body);
            titulo     = node.path("titulo").asText().trim();
            autor      = node.path("autor").asText().trim();
            isbn       = node.path("isbn").asText("").trim();
            disponivel = node.path("disponivel").asBoolean(true);
        } catch (Exception e) {
            sendResponse(exchange, 400, "JSON inválido");
            return;
        }

        try {
            String resultado = service.atualizarLivro(id, titulo, autor, isbn, disponivel);
            if (resultado.contains("não encontrado")) {
                sendResponse(exchange, 404, resultado);
            } else if (resultado.contains("Erro")) {
                sendResponse(exchange, 500, resultado);
            } else {
                sendResponse(exchange, 200, resultado);
            }
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, e.getMessage());
        } catch (Exception e) {
            sendResponse(exchange, 500, "Erro interno ao atualizar livro");
        }
    }

    // -----------------------------------------------------------------------
    // PATCH /api/livros/{id} → só altera disponibilidade
    // { "disponivel": true }
    // -----------------------------------------------------------------------
    private void handlePatch(HttpExchange exchange, String path) throws IOException {
        int id = extrairId(exchange, path);
        if (id < 0) return;

        String body = readBody(exchange);
        if (body == null) {
            sendResponse(exchange, 400, "Corpo da requisição muito grande ou ausente");
            return;
        }

        boolean disponivel;
        try {
            JsonNode node = objectMapper.readTree(body);
            if (!node.has("disponivel")) {
                sendResponse(exchange, 400, "Campo 'disponivel' é obrigatório");
                return;
            }
            disponivel = node.get("disponivel").asBoolean();
        } catch (Exception e) {
            sendResponse(exchange, 400, "JSON inválido");
            return;
        }

        try {
            String resultado = service.alterarDisponibilidade(id, disponivel);
            if (resultado.contains("não encontrado")) {
                sendResponse(exchange, 404, resultado);
            } else if (resultado.contains("Erro")) {
                sendResponse(exchange, 500, resultado);
            } else {
                sendResponse(exchange, 200, resultado);
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "Erro interno ao alterar disponibilidade");
        }
    }

    // -----------------------------------------------------------------------
    // DELETE /api/livros/{id}
    // -----------------------------------------------------------------------
    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        int id = extrairId(exchange, path);
        if (id < 0) return;

        try {
            String resultado = service.removerLivro(id);
            if (resultado.contains("não encontrado")) {
                sendResponse(exchange, 404, resultado);
            } else if (resultado.contains("Erro")) {
                sendResponse(exchange, 500, resultado);
            } else {
                sendResponse(exchange, 200, resultado);
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "Erro interno ao remover livro");
        }
    }

    // -----------------------------------------------------------------------
    // Utilitários
    // -----------------------------------------------------------------------

    /** Extrai o ID numérico do path e já envia 400 se inválido. */
    private int extrairId(HttpExchange exchange, String path) throws IOException {
        String[] parts = path.split("/");
        if (parts.length < 4) {
            sendResponse(exchange, 400, "Informe o ID do livro na URL: /api/livros/{id}");
            return -1;
        }
        try {
            return Integer.parseInt(parts[parts.length - 1]);
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "ID inválido");
            return -1;
        }
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            byte[] buffer = new byte[MAX_BODY_BYTES + 1];
            int bytesRead = is.read(buffer);
            if (bytesRead > MAX_BODY_BYTES) return null;
            if (bytesRead <= 0) return "";
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
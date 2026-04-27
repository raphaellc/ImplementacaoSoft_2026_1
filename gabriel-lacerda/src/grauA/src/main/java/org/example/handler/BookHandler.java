package org.example.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.service.BookService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class BookHandler implements HttpHandler {
    private final BookService service;
    private final ObjectMapper mapper = new ObjectMapper();

    public BookHandler(BookService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Content-Type", "application/json");

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("GET".equals(method)) {
            byte[] response = mapper.writeValueAsBytes(service.getAllBooks());
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(response); }

        } else if ("POST".equals(method)) {
            InputStream is = exchange.getRequestBody();
            Map<?, ?> body = mapper.readValue(is, Map.class);
            try {
                service.addBook((String) body.get("title"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            byte[] response = "{\"message\":\"Livro adicionado!\"}".getBytes();
            exchange.sendResponseHeaders(201, response.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(response); }

        } else if ("PUT".equals(method)) {
            String[] parts = path.split("/");
            int id = Integer.parseInt(parts[parts.length - 1]);
            try {
                service.rentBook(id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            byte[] response = "{\"message\":\"Livro alugado!\"}".getBytes();
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(response); }

        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}

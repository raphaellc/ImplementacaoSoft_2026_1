package org.example.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.OutputStream;
import java.io.IOException;

public class BookHandler implements HttpHandler {
    private final BookService service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BookHandler(BookService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        if ("GET".equals(exchange.getRequestMethod())) {
            var books = service.getAll();
            byte[] response = objectMapper.writeValueAsBytes(books);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}
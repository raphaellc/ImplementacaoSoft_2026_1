package com.gerenciadortarefas.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerenciadortarefas.service.TarefaService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.OutputStream;
import java.io.IOException;

public class TarefaHandler implements HttpHandler {
    private TarefaService service;
    private ObjectMapper objectMapper = new ObjectMapper();

    public TarefaHandler(TarefaService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        if ("GET".equals(exchange.getRequestMethod())) {
            var tarefas = service.listarTarefas();
            byte[] response = objectMapper.writeValueAsBytes(tarefas);
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }
}
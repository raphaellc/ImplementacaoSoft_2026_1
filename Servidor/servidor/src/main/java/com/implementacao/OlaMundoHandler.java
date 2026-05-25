package com.implementacao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class OlaMundoHandler implements HttpHandler {
    private static final int MAX_BODY_BYTES = 1024; // 1 KB
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException{
        //String response = "Olá, mundo!";
        //byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        
        // 1. Configura os headers (antes de enviar)
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");

        String metodo = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        System.out.println(path);
        if("OPTIONS".equals(metodo)){
            exchange.sendResponseHeaders(204, -1);
        }
        if("GET".equals(metodo)){
            this.handleGet(exchange);
        }
        if("POST".equals(metodo)){
            this.handlePost(exchange);
        }
    }
    public void handleGet(HttpExchange exchange) throws IOException {
        String mensagem  = "Olá Mundo";
        // byte[] response = objectMapper.writeValueAsBytes(mensagem);
        // exchange.getResponseHeaders().set("Content-Type", "application/json");
        // exchange.sendResponseHeaders(200, response.length);
        // try (OutputStream os = exchange.getResponseBody()) {
        //     os.write(response);
        //     os.close();
        // }
        sendResponse(exchange, 200, mensagem);
    }

    public void handlePost(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        if (body == null) {
            sendResponse(exchange, 400, "Corpo da requisição muito grande ou ausente");
            return;
        }

        String nome;
        try {
            JsonNode node = objectMapper.readTree(body);
            JsonNode campo = node.get("nome");
            if (campo == null || campo.isNull()) {
                sendResponse(exchange, 400, "Campo 'nome' é obrigatório");
                return;
            }
            nome = campo.asText().trim();
        } catch (Exception e) {
            sendResponse(exchange, 400, "JSON inválido");
            return;
        }

        if (nome.isBlank()) {
            sendResponse(exchange, 400, "O nome não pode ser vazio");
            return;
        }
        if (nome.length() > 255) {
            sendResponse(exchange, 400, "O nome deve ter no máximo 255 caracteres");
            return;
        }

        try {
            String mensagemRetorno = "Olá Mundo, " + nome + "!";
            ObjectNode responseNode = objectMapper.createObjectNode();
            responseNode.put("mensagem", mensagemRetorno);
            
            byte[] response = objectMapper.writeValueAsBytes(responseNode);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
            
        } catch (Exception e) {
            sendResponse(exchange, 500, "Erro interno ao processar a requisição");
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
}

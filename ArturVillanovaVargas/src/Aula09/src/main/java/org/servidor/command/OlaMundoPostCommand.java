package org.servidor.command;

import com.sun.net.httpserver.HttpExchange;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.servidor.command.Command;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class OlaMundoPostCommand implements Command {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void execute(HttpExchange exchange) throws IOException {

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        String response;
        int status;

        JsonNode node;
        try {
            node = mapper.readTree(body);
        } catch (JsonParseException e) {
            response = mapper.writeValueAsString(
                    mapper.createObjectNode().put("mensagem", "JSON inválido")
            );
            sendResponse(exchange, 400, response);
            return;
        }

        if (node == null || !node.has("nome") || node.get("nome").isNull()) {
            response = mapper.writeValueAsString(
                    mapper.createObjectNode().put("mensagem", "Campo 'nome' é obrigatório")
            );
            sendResponse(exchange, 400, response);
            return;
        }

        String nome = node.get("nome").asText();

        if (nome.isBlank()) {
            response = mapper.writeValueAsString(
                    mapper.createObjectNode().put("mensagem", "O nome não pode ser vazio")
            );
            sendResponse(exchange, 400, response);
            return;
        }

        response = mapper.writeValueAsString(
                mapper.createObjectNode().put("mensagem", "Olá Mundo, " + nome + "!")
        );
        sendResponse(exchange, 200, response);
    }

    private void sendResponse(HttpExchange exchange, int status, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
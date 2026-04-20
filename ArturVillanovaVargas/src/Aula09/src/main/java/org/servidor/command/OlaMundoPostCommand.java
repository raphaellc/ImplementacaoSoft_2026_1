package Aula09.src.main.java.org.servidor.command;

import com.sun.net.httpserver.HttpExchange;
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

        JsonNode node = mapper.readTree(body);
        String nome = node.get("nome").asText();



        String response = null;
        int status;

        if(nome == null) {
            response =  mapper.writeValueAsString(
                    mapper.createObjectNode().put("mensagem", "Olá Mundo, " + nome)
            );
            status = 200;

        } else {
            response = mapper.writeValueAsString(
                    mapper.createObjectNode().put("mensagem", "Campo 'nome' é obrigatório")
            );

            status = 400;
        }



        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, response.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}
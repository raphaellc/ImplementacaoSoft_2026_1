package AugustoFeltrin.Aula06.Etapa03;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class OlaMundoPostHandler implements HttpHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
            if("POST".equalsIgnoreCase(exchange.getRequestMethod())){
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            JsonNode node = objectMapper.readTree(body);
            String nome = (node.has("nome")) ? node.get("nome").asText() : "Visitante";

            ObjectNode respondeNode = objectMapper.createObjectNode();
            respondeNode.put("mensagem", "Olá mundo, " + nome + "!");
            byte[] responseBytes = objectMapper.writeValueAsBytes(respondeNode);

            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);

            try(OutputStream os = exchange.getResponseBody()){
                os.write(responseBytes);
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}


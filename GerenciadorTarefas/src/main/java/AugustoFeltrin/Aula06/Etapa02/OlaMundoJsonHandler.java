package AugustoFeltrin.Aula06.Etapa02;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;

public class OlaMundoJsonHandler implements HttpHandler{
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException{
        ObjectNode responseNode = objectMapper.createObjectNode();
        responseNode.put("mensagem", "Olá Mundo");

        byte[] bytes = objectMapper.writeValueAsBytes(responseNode);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);

        try(OutputStream os = exchange.getResponseBody()){
            os.write(bytes);
        }
    }
}

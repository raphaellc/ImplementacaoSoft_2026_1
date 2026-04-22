package org.servidor.command;

import com.sun.net.httpserver.HttpExchange;
import org.servidor.command.Command;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class OlaMundoGetCommand implements Command {

    @Override
    public void execute(HttpExchange exchange) throws IOException {
        String response = "{\"mensagem\": \"Olá Mundo\"}";

        exchange.getResponseHeaders().set("Content-Type", "application/json;");
        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
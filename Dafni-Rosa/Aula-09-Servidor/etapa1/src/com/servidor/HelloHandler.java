package com.servidor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

// Etapa 1: Handler simples

public class HelloHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange troca) throws IOException {
        String metodo = troca.getRequestMethod();

        // Apenas o GET é permitido nesta etapa
        if (!"GET".equalsIgnoreCase(metodo)) {
            troca.sendResponseHeaders(405, -1); // 405 Method Not Allowed
            troca.close();
            return;
        }

        String resposta = "Hello, World!";
        byte[] bytes = resposta.getBytes(StandardCharsets.UTF_8);

        // Define o tipo de conteúdo como texto puro
        troca.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        troca.sendResponseHeaders(200, bytes.length);

        try (OutputStream saida = troca.getResponseBody()) {
            saida.write(bytes);
        }

        System.out.println("[GET] /api/hello -> 200 OK");
    }
}

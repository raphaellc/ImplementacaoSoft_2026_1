package com.implementacao;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class OlaMundoHandler implements HttpHandler {
 
    @Override
    public void handle(HttpExchange exchange) throws IOException{
        String response = "Olá, mundo!";
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        
        // 1. Configura os headers (antes de enviar)
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");

        String metodo = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        System.out.println(path);
        if("OPTIONS".equals(metodo)){
            exchange.sendResponseHeaders(204, -1);
        }
        if("GET".equals(metodo)){
            exchange.sendResponseHeaders(200, bytes.length);
        }
        // 3. Escreve o corpo
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
            os.close();
        }
    }
}


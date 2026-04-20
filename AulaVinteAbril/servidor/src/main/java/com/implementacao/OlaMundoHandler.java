package com.implementacao;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class OlaMundoHandler implements HttpHandler {
    //Fazer o override do handler
    @Override
    public void handle(HttpExchange exchange) throws IOException{
        //Configura a mensagem de resposta
        String response = "Olá Mundo";
        //pega o número de bytes da mensagem e configura para fazer o stream via rede
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        //Configura os headers
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        //Envia os headers, ou prepara para o envio.
        exchange.sendResponseHeaders(200, bytes.length);
        //caso de certo envia.
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}

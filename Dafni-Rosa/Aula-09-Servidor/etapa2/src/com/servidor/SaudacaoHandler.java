package com.servidor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;

//Etapa 2: Handler com resposta JSON
 
public class SaudacaoHandler implements HttpHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange troca) throws IOException {
        String metodo = troca.getRequestMethod();

        if ("OPTIONS".equalsIgnoreCase(metodo)) {
            troca.sendResponseHeaders(204, -1);
            troca.close();
            return;
        }

        if (!"GET".equalsIgnoreCase(metodo)) {
            enviarErroJson(troca, 405, "Método não permitido. Use GET.");
            return;
        }

        processarGet(troca);
    }

    private void processarGet(HttpExchange troca) throws IOException {

        ObjectNode corpo = mapper.createObjectNode();
        corpo.put("mensagem", "Hello, World!");
        corpo.put("status", "ok");

        byte[] bytes = mapper.writeValueAsBytes(corpo);

        troca.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        troca.sendResponseHeaders(200, bytes.length);

        try (OutputStream saida = troca.getResponseBody()) {
            saida.write(bytes);
        }

        System.out.println("[GET] /api/saudacao -> 200 OK (JSON)");
    }

    private void enviarErroJson(HttpExchange troca, int status, String mensagemErro) throws IOException {
        ObjectNode erro = mapper.createObjectNode();
        erro.put("erro", mensagemErro);

        byte[] bytes = mapper.writeValueAsBytes(erro);
        troca.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        troca.sendResponseHeaders(status, bytes.length);

        try (OutputStream saida = troca.getResponseBody()) {
            saida.write(bytes);
        }
    }
}

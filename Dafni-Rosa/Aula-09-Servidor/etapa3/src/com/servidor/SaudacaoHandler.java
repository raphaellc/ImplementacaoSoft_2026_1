package com.servidor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

// Etapa 3: Handler com GET e POST
 
public class SaudacaoHandler implements HttpHandler {

    private static final int LIMITE_CORPO_BYTES = 2048; // 2 KB
    private static final int TAMANHO_MAX_NOME   = 100;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange troca) throws IOException {
        String metodo = troca.getRequestMethod();

        switch (metodo.toUpperCase()) {
            case "OPTIONS":
  
                troca.sendResponseHeaders(204, -1);
                troca.close();
                break;
            case "GET":
                processarGet(troca);
                break;
            case "POST":
                processarPost(troca);
                break;
            default:
                enviarRespostaJson(troca, 405, "erro", "Método '" + metodo + "' não é suportado neste endpoint.");
        }
    }

    // GET: retorna saudação genérica
    private void processarGet(HttpExchange troca) throws IOException {
        ObjectNode resposta = mapper.createObjectNode();
        resposta.put("mensagem", "Hello, World!");
        resposta.put("status", "ok");

        escreverJson(troca, 200, resposta);
        System.out.println("[GET] 200 -> saudação padrão");
    }

    // POST: lê o corpo, valida e retorna saudação personalizada
    private void processarPost(HttpExchange troca) throws IOException {
    
        String corpoTexto = lerCorpo(troca);
        if (corpoTexto == null) {
            enviarRespostaJson(troca, 400, "erro", "Corpo da requisição ausente ou excede o tamanho permitido.");
            return;
        }

        JsonNode raiz;
        try {
            raiz = mapper.readTree(corpoTexto);
        } catch (Exception e) {
            enviarRespostaJson(troca, 400, "erro", "JSON inválido. Certifique-se de enviar um JSON bem formado.");
            return;
        }

        JsonNode campoNome = raiz.get("nome");
        if (campoNome == null || campoNome.isNull()) {
            enviarRespostaJson(troca, 400, "erro", "O campo 'nome' é obrigatório no corpo da requisição.");
            return;
        }

        String nome = campoNome.asText().trim();
        if (nome.isEmpty()) {
            enviarRespostaJson(troca, 400, "erro", "O campo 'nome' não pode ser vazio ou conter apenas espaços.");
            return;
        }
        if (nome.length() > TAMANHO_MAX_NOME) {
            enviarRespostaJson(troca, 400, "erro",
                    "O nome deve ter no máximo " + TAMANHO_MAX_NOME + " caracteres.");
            return;
        }

        ObjectNode resposta = mapper.createObjectNode();
        resposta.put("mensagem", "Hello, " + nome + "!");

        escreverJson(troca, 200, resposta);
        System.out.println("[POST] 200 -> saudação para: " + nome);
    }


    private String lerCorpo(HttpExchange troca) throws IOException {
        try (InputStream entrada = troca.getRequestBody();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            byte[] bloco = new byte[256];
            int lidos;
            int totalLido = 0;

            while ((lidos = entrada.read(bloco)) != -1) {
                totalLido += lidos;
                if (totalLido > LIMITE_CORPO_BYTES) {
                    return null; // corpo excede o limite permitido
                }
                buffer.write(bloco, 0, lidos);
            }

            return buffer.toString(StandardCharsets.UTF_8);
        }
    }

    private void escreverJson(HttpExchange troca, int status, ObjectNode corpo) throws IOException {
        byte[] bytes = mapper.writeValueAsBytes(corpo);
        troca.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        troca.sendResponseHeaders(status, bytes.length);
        try (OutputStream saida = troca.getResponseBody()) {
            saida.write(bytes);
        }
    }

    private void enviarRespostaJson(HttpExchange troca, int status, String chave, String valor) throws IOException {
        ObjectNode node = mapper.createObjectNode();
        node.put(chave, valor);
        escreverJson(troca, status, node);
        System.out.println("[" + troca.getRequestMethod() + "] " + status + " -> " + valor);
    }
}

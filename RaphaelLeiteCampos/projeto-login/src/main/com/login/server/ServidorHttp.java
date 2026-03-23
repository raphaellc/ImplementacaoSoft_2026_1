package com.login.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.google.gson.Gson;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

/**
 * SERVIDOR HTTP EMBUTIDO
 * ======================
 * Java possui um servidor HTTP nativo (com.sun.net.httpserver)
 * que não precisa de Spring ou Tomcat.
 *
 * Este wrapper encapsula a configuração e adiciona:
 *  - CORS automático (para o front em HTML poder chamar a API)
 *  - Serialização/desserialização JSON com Gson
 *  - Resposta padronizada
 *
 * É aqui que fica claro O QUE o Spring Boot faz "por baixo dos panos"!
 */
public class ServidorHttp {

    private final HttpServer server;
    public static final Gson GSON = new Gson();
    private final int porta;

    public ServidorHttp(int porta) throws IOException {
        this.porta = porta;
        this.server = HttpServer.create(new InetSocketAddress(porta), 0);
        // Pool de threads para atender múltiplas requisições simultâneas
        this.server.setExecutor(Executors.newFixedThreadPool(4));
    }

    public void registrarRota(String caminho, RotaHandler handler) {
        server.createContext(caminho, exchange -> {
            // Adiciona cabeçalhos CORS — permite chamadas do front HTML
            adicionarCors(exchange);

            // Responde ao preflight OPTIONS (requisição de verificação do browser)
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                responder(exchange, 200, "{}");
                return;
            }

            try {
                handler.handle(exchange);
            } catch (Exception e) {
                System.err.println("Erro na rota " + caminho + ": " + e.getMessage());
                responder(exchange, 500, GSON.toJson(new ErroResponse("Erro interno do servidor.")));
            }
        });
    }

    public void iniciar() {
        server.start();
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║  Servidor rodando em ::" + porta + "           ║");
        System.out.println("║  POST /api/cadastro                  ║");
        System.out.println("║  POST /api/login                     ║");
        System.out.println("║  Abra: frontend/index.html           ║");
        System.out.println("╚══════════════════════════════════════╝");
    }

    // ───── Métodos auxiliares ─────

    public static String lerCorpo(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static void responder(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void adicionarCors(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    @FunctionalInterface
    public interface RotaHandler {
        void handle(HttpExchange exchange) throws IOException;
    }

    // DTO interno para erros genéricos
    public record ErroResponse(String erro) {}
}
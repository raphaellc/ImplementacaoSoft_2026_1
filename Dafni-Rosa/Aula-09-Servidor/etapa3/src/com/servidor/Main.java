package com.servidor;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

// Etapa 3: Servidor HTTP com GET e POST

public class Main {

    public static void main(String[] args) {
        final int PORTA = 8080;

        HttpServer servidor;
        try {
            servidor = HttpServer.create(new InetSocketAddress(PORTA), 0);
        } catch (IOException e) {
            System.err.println("Não foi possível iniciar o servidor: " + e.getMessage());
            return;
        }

        servidor.createContext("/api/saudacao", new SaudacaoHandler());

        servidor.setExecutor(Executors.newFixedThreadPool(10));
        servidor.start();

        System.out.println("Servidor rodando em http://localhost:" + PORTA + "/api/saudacao");
        System.out.println("  GET  -> retorna saudação padrão");
        System.out.println("  POST -> corpo JSON { \"nome\": \"SeuNome\" }");
    }
}

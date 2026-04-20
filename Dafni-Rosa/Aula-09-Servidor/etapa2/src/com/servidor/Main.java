package com.servidor;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

// Etapa 2: Servidor HTTP com resposta em JSON

public class Main {

    public static void main(String[] args) {
        final int PORTA = 8080;

        HttpServer servidor;
        try {
            servidor = HttpServer.create(new InetSocketAddress(PORTA), 0);
        } catch (IOException e) {
            System.err.println("Falha ao criar o servidor na porta " + PORTA + ": " + e.getMessage());
            return;
        }

        servidor.createContext("/api/saudacao", new SaudacaoHandler());

        servidor.setExecutor(Executors.newFixedThreadPool(5));
        servidor.start();

        System.out.println("Servidor ativo em http://localhost:" + PORTA + "/api/saudacao");
    }
}

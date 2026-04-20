package com.servidor;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

//Etapa 1: Servidor HTTP simples
public class Main {

    public static void main(String[] args) {
        final int PORTA = 8080;

        HttpServer servidor;
        try {
            servidor = HttpServer.create(new InetSocketAddress(PORTA), 0);
        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
            return;
        }

        servidor.createContext("/api/hello", new HelloHandler());

        servidor.setExecutor(Executors.newFixedThreadPool(5));

        servidor.start();
        System.out.println("Servidor rodando em http://localhost:" + PORTA + "/api/hello");
    }
}

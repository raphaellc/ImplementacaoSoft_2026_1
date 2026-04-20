package com.implementacao;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import com.sun.net.httpserver.HttpServer;
/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        //Criação do server
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/api/ola-mundo", new OlaMundoHandler());
            server.setExecutor(Executors.newFixedThreadPool(10));
            server.start();
        System.out.println("API rodando em http://localhost:8080/api/ola-mundo");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

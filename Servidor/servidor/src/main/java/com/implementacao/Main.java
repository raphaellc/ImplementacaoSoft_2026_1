package com.implementacao;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import com.implementacao.OlaMundoHandler;
public class Main {
    public static void main(String[] args) {
        //1. Criar o server
        HttpServer server = null;
        //2. Configurar o Server, escutando na porta 8080
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
            }
        //3.Criar Contexto - criação de endpoints
        //O que são endpoints?
        server.createContext("/api/ola-mundo", new OlaMundoHandler());
        //4.Definir método de gerencialmento de requisições
        //Configura a execução de até 10 threads simultâneas   
        server.setExecutor(Executors.newFixedThreadPool(10));
        //5. Iniciar o server
        server.start();
        System.out.println("API rodando em http://localhost:8080/api/ola-mundo");
        System.out.println("Frontend disponível em http://localhost:8080/");
    }
}
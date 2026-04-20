package Aula09.src.main.java.org.servidor;

import com.sun.net.httpserver.HttpServer;
import org.servidor.routes.RouterHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class App {
    public static void main(String[] args) throws Exception {

        HttpServer server = null;
        server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/api/ola-mundo", new RouterHandler());

        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();

        System.out.println("Servidor rodando em http://localhost:8080/api/ola-mundo");
    }
}
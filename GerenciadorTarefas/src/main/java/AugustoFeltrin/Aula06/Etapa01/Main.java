package AugustoFeltrin.Aula06.Etapa01;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException{
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/api/ola-mundo", new OlaMundoHandler());
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        System.out.println("Etapa 1 rodando em http://localhost:8080/api/ola-mundo");
    }    
}

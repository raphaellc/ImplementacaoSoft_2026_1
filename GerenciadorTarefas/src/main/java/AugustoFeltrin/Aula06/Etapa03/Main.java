package AugustoFeltrin.Aula06.Etapa03;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/api/post", new OlaMundoPostHandler());
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();

        System.out.println("Etapa 3 rodando em http://localhost:8080/api/post");
    }
}

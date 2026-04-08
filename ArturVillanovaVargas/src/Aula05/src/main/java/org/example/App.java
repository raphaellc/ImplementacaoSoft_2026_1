package org.example;

import com.sun.net.httpserver.HttpServer;
import org.example.api.BookHandler;
import org.example.controller.BookController;
import org.example.repository.BookRepository;
import org.example.service.BookService;
import org.example.view.BookView;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App
{
    static void main()  {
        BookRepository repository = new BookRepository();
        BookController controller = new BookController(new BookView(),new BookService(repository));

        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
        } catch (IOException e) {
            System.out.println("Erro ao inicializar servidor:" + e.getMessage());
            return;
        }

        server.createContext("/api/books", new BookHandler(new BookService(repository)));

        server.setExecutor(null);
        server.start();
        System.out.println("API rodando em http://localhost:8080/api/books");

        try {
            controller.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

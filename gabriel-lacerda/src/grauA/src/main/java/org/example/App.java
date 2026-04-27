package org.example;

import com.sun.net.httpserver.HttpServer;
import org.example.handler.BookHandler;
import org.example.controller.BookController;
import org.example.repository.BookRepository;
import org.example.service.BookService;
import org.example.view.BookView;

import java.net.InetSocketAddress;

public class App {
    public static void main(String[] args) throws Exception {
        BookRepository repository = new BookRepository();
        BookService service = new BookService(repository);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/books", new BookHandler(service));
        server.start();
        System.out.println("API rodando em http://localhost:8080/api/books");

        BookView view = new BookView();
        BookController controller = new BookController(view, repository);
        controller.start();
    }
}
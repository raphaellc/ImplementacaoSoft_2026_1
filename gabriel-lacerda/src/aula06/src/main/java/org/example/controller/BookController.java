package aula06.src.main.java.org.example.controller;

import aula06.src.main.java.org.example.repository.BookRepository;
import aula06.src.main.java.org.example.view.BookView;
import aula06.src.main.java.org.example.service.BookService;

public class BookController {
    private final BookView view;
    private final BookRepository repository;
    private final BookService service;

    public BookController(BookView view, BookRepository repository) {
        this.view = view;
        this.repository = repository;
        this.service = new BookService(repository);
    }

    public void start() throws InterruptedException {
        boolean running = true;

        while (running) {
            String choice = view.menu();

            switch (choice) {
                case "1" -> getAllBooks();
                case "2" -> addBook();
                case "3" -> rentBook();
                case "0" -> running = false;
            }
        }
    }

    public void getAllBooks() {
        view.getAllBooks(repository.getAllBooks());
    }

    public void addBook() throws InterruptedException {
        String title = view.addBook();

        try {
            service.addBook(title);
            Thread.sleep(1000);
            view.addSuccess();
        } catch (Exception e) {
            view.showError(e.getMessage());
        }

        Thread.sleep(1000);
    }

    public void rentBook() throws InterruptedException {
        int id = view.rentBook();

        try {
            service.rentBook(id);
            Thread.sleep(1000);
            view.rentSuccess();
        } catch (Exception e) {
            view.showError(e.getMessage());
        }

        Thread.sleep(1000);
    }
}

package Aula05.src.main.java.org.example.controller;

import Aula05.src.main.java.org.example.repository.BookRepository;
import Aula05.src.main.java.org.example.view.BookView;

public class BookController {
    private final BookView view;
    private final BookRepository repository;

    public BookController(BookView view, BookRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    public void start() throws InterruptedException {
        boolean i = true;

        while (i) {
            String choice = view.menu();

            switch (choice) {
                case "1":
                    getAllBooks();
                    break;
                case "2":
                    addBook();
                    break;
                case "3":
                    rentBook();
                    break;
                case "0":
                    i = false;
            }
        }
    }

    public void addBook() throws InterruptedException {
        String title = view.addBook();
        repository.addBook(title);

        Thread.sleep(1000);
        view.addSuccess();
        Thread.sleep(1000);
    }

    public void rentBook(){
        int id = view.rentBook();

        try {
            repository.rentBook(id);

            Thread.sleep(1000);
            view.rentSuccess();
            Thread.sleep(1000);

        } catch (Exception e) {
            view.rentException(e.getMessage());
        }
    }

    public void getAllBooks(){
        view.getAllBooks(repository.getAllBooks());
    }
}

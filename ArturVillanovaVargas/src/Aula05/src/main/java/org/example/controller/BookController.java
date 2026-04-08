package org.example.controller;

import org.example.service.BookService;
import org.example.view.BookView;

public class BookController {
    private final BookView view;
    private final BookService service;

    public BookController(BookView view, BookService service) {
        this.view = view;
        this.service = service;
    }

    public void run() throws InterruptedException {
        boolean i = true;

        while (i) {
            String choice = view.menu();

            switch (choice) {
                case "1":
                    getAll();
                    break;
                case "2":
                    add();
                    break;
                case "3":
                    rent();
                    break;
                case "4":
                    remove();
                    break;
                case "0":
                    i = false;
            }
        }
    }

    public void add() throws InterruptedException {
        String title = view.addBook();
        service.add(title);

        Thread.sleep(1000);
        view.showSuccess("Livro adicionado com sucesso!");
        Thread.sleep(1000);
    }

    public void rent(){
        int id = view.rentBook();

        try {
            String message = service.rent(id);

            Thread.sleep(1000);
            view.showSuccess(message);
            Thread.sleep(1000);

        } catch (Exception e) {
            view.showException(e.getMessage());
        }
    }

    public void remove() {
        int id = view.removeBook();

        try {
            String message = service.remove(id);

            Thread.sleep(1000);
            view.showSuccess(message);
            Thread.sleep(1000);

        } catch (Exception e) {
            view.showException(e.getMessage());
        }
    }

    public void getAll(){
        view.getAllBooks(service.getAll());
    }
}

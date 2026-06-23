package br.com.gerenciadorlivros.controller;

import br.com.gerenciadorlivros.service.BookService;
import br.com.gerenciadorlivros.repository.BookRepository;
import br.com.gerenciadorlivros.view.BookView;

public class BookController {

    private final BookView view;
    private final BookService service;

    public BookController(BookView view, BookRepository repository) {
        this.view = view;
        this.service = new BookService(repository);
    }

    public void start() throws Exception {
        while (true) {
            String option = view.menu();

            switch (option) {
                case "1":
                    view.showBooks(service.getAllBooks());
                    break;

                case "2":
                    try {
                        service.addBook(view.addBook());
                        view.addSuccess();
                    } catch (Exception e) {
                        view.showError(e.getMessage());
                    }
                    break;

                case "3":
                    try {
                        service.rentBook(view.rentBook());
                        view.rentSuccess();
                    } catch (Exception e) {
                        view.showError(e.getMessage());
                    }
                    break;

                case "0":
                    return;

                default:
                    view.showError("Opção inválida!");
            }
        }
    }
}
package org.example.service;

import org.example.model.Book;
import org.example.repository.BookRepository;

import java.util.Optional;

public class BookService {
    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public void addBook(String title) throws Exception {
        if (title == null || title.isBlank()) {
            throw new Exception("O livro precisa ter um título.");
        }
        repository.addBook(title);
    }

    public void rentBook(int id) throws Exception {
        Optional<Book> found = repository.findById(id);

        if (found.isEmpty()) {
            throw new Exception("Livro de id " + id + " não encontrado.");
        }

        if (!found.get().available()) {
            throw new Exception("O livro \"" + found.get().title() + "\" já está alugado.");
        }

        Book updated = new Book(found.get().id(), found.get().title(), false);
        repository.update(updated);
    }

    public java.util.List<Book> getAllBooks() {
        return repository.getAllBooks();
    }
}
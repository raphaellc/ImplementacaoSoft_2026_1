package aula06.src.main.java.org.example.service;

import aula06.src.main.java.org.example.model.Book;
import aula06.src.main.java.org.example.repository.BookRepository;
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

        repository.rentBook(id);
    }
}
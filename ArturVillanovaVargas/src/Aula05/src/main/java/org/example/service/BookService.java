package org.example.service;

import org.example.model.Book;
import org.example.repository.BookRepository;

import java.util.List;
import java.util.Optional;

public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public void add(String title) {
        repository.add(title);
    }

    public String rent(int id) {

        Optional<Book> bookOptional = repository.getById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            Book rentBook = book.setAvailable();
            if (repository.update(rentBook)) {
                return "Livro " + id + " marcado como alugado.";
            }
            return "Erro ao alugar o livro.";
        }
        return "Livro com ID " + id + " não encontrado.";
    }

    public String remove(int id) {
        Optional<Book> bookOptional = repository.getById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            if (repository.remove(book)) {
                return "Livro " + id + " excluido.";
            }
            return "Erro ao excluir livro.";
        }
        return "Livro com ID " + id + " não encontrado.";
    }

    public List<Book> getAll() {
        return repository.getAll();
    }
}

package br.com.gerenciadorlivros.repository;

import br.com.gerenciadorlivros.model.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepository {
    private final List<Book> bookList = new ArrayList<>();
    private int nextId = 1;

    public void addBook(String title) {
        bookList.add(new Book(nextId++, title, true));
    }

    public void rentBook(int id) {
        for (int i = 0; i < bookList.size(); i++) {
            Book book = bookList.get(i);
            if (book.id() == id) {
                bookList.set(i, new Book(book.id(), book.title(), false));
                return;
            }
        }
    }

    public Optional<Book> findById(int id) {
        return bookList.stream()
                .filter(b -> b.id() == id)
                .findFirst();
    }

    public List<Book> getAllBooks() {
        return bookList;
    }
}
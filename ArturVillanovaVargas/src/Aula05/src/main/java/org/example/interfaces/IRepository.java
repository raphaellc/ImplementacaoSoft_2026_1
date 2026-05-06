package org.example.interfaces;

import org.example.model.Book;

import java.util.List;
import java.util.Optional;

public interface IRepository {
    void add(String title);
    boolean update(Book book);
    boolean remove(Book book);
    Optional<Book> getById(int id);
    List<Book> getAll();
}

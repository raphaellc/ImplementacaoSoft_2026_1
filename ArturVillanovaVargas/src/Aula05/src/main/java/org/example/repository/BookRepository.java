package Aula05.src.main.java.org.example.repository;

import Aula05.src.main.java.org.example.model.Book;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {
    private final List<Book> bookList = new ArrayList<>();
    private int nextId = 1;

    public void addBook(String title) {
        bookList.add(new Book(nextId++, title, true));
    }

    public void rentBook(int id) throws Exception {
        for (int i = 0; i < bookList.size(); i++) {
            Book book = bookList.get(i);

            if(book.id() == id) {
                if(!book.available()) throw new Exception("O livro já está alugado.");

                bookList.set(i, new Book(book.id(), book.title(), false));
                return;
            }
        }

        throw new Exception("O livro de id "+ id +", não foi encontrado.");
    }

    public List<Book> getAllBooks() {
        return bookList;
    }
}

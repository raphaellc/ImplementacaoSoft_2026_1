package aula06.src.main.java.org.example;

import aula06.src.main.java.org.example.controller.BookController;
import aula06.src.main.java.org.example.repository.BookRepository;
import aula06.src.main.java.org.example.view.BookView;

public class App
{
    static void main()  {
        BookController controller = new BookController(new BookView(),new BookRepository());

        try {
            controller.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

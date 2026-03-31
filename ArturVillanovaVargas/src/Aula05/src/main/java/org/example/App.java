package Aula05.src.main.java.org.example;

import Aula05.src.main.java.org.example.controller.BookController;
import Aula05.src.main.java.org.example.repository.BookRepository;
import Aula05.src.main.java.org.example.view.BookView;

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

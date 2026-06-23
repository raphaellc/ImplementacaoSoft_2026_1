package br.com.gerenciadorlivros;

import br.com.gerenciadorlivros.controller.BookController;
import br.com.gerenciadorlivros.repository.BookRepository;
import br.com.gerenciadorlivros.view.BookView;

public class App {
    public static void main(String[] args) {
        BookController controller = new BookController(
                new BookView(),
                new BookRepository()
        );

        try {
            controller.start();
        } catch (Exception e) {
            System.out.println("Erro na aplicação: " + e.getMessage());
        }
    }
}
package org.example.view;

import org.example.model.Book;
import java.util.List;
import java.util.Scanner;

public class BookView {
    Scanner sc = new Scanner(System.in);

    public String menu() {
        System.out.println("MENU");
        System.out.println("-----------------");
        System.out.println("1. LISTAR LIVROS");
        System.out.println("2. ADICIONAR LIVRO");
        System.out.println("3. ALUGAR LIVRO");
        System.out.println("4. REMOVER LIVRO");
        System.out.println("0. SAIR");

        return sc.nextLine();
    }

    public void getAllBooks(List<Book> books){
        System.out.println(books.toString());
    }

    public String addBook(){
        System.out.println("Digite o titulo do livro que deseja adicionar:");
        return sc.nextLine();
    }

    public int rentBook(){
        System.out.println("Digite o id do livro que deseja alugar:");
        return Integer.parseInt(sc.nextLine());
    }

    public int removeBook() {
        System.out.println("Digite o id do livro que deseja remover:");
        return Integer.parseInt(sc.nextLine());
    }

    public void showException(String message) {
        System.out.println(message);
    }

    public void showSuccess(String message) {
        System.out.println(message);
    }
}

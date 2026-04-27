package org.example.view;

import org.example.model.Book;

import java.util.List;
import java.util.Scanner;

public class BookView {
    Scanner sc = new Scanner(System.in);

    public String menu() {
        System.out.println("ESCOLHA OU MORRA");
        System.out.println("-----------------");
        System.out.println("1. LISTAR LIVROS");
        System.out.println("2. ADICIONAR LIVRO");
        System.out.println("3. ALUGAR LIVRO");
        System.out.println("0. SAIR");
        return sc.nextLine();
    }

    public void getAllBooks(List<Book> books) {
        System.out.println(books.toString());
    }

    public String addBook() {
        System.out.println("Digite o titulo ou morra:");
        return sc.nextLine();
    }

    public int rentBook() {
        System.out.println("Digite o id do livro ou morra:");
        return Integer.parseInt(sc.nextLine());
    }

    public void rentSuccess() {
        System.out.println("Livro alugado com sucesso!");
    }

    public void addSuccess() {
        System.out.println("Livro adicionado com sucesso!");
    }

    public void showError(String message) {
        System.out.println("Erro: " + message);
    }
}
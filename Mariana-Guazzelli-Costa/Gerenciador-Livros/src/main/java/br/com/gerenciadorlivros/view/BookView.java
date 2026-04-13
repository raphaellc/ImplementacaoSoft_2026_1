package br.com.gerenciadorlivros.view;

import br.com.gerenciadorlivros.model.Book;

import java.util.List;
import java.util.Scanner;

public class BookView {

    private final Scanner sc = new Scanner(System.in);

    public String menu() {
        System.out.println("\n=== GERENCIADOR DE LIVROS ===");
        System.out.println("1. Listar livros");
        System.out.println("2. Adicionar livro");
        System.out.println("3. Alugar livro");
        System.out.println("0. Sair");
        return sc.nextLine();
    }

    public void showBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
            return;
        }

        for (Book b : books) {
            System.out.println("ID: " + b.id() +
                    " | Título: " + b.title() +
                    " | Disponível: " + (b.available() ? "Sim" : "Não"));
        }
    }

    public String addBook() {
        System.out.print("Digite o título do livro: ");
        return sc.nextLine();
    }

    public int rentBook() {
        System.out.print("Digite o ID do livro: ");
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
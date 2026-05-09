package br.edu.projeto.view;

import br.edu.projeto.model.Livro;

import java.util.List;
import java.util.Scanner;

public class LivroView {

    private Scanner scanner = new Scanner(System.in);

    public String mostrarMenu() {
        System.out.println("\n=== MENU ===");
        System.out.println("1 - Adicionar livro");
        System.out.println("2 - Listar livros");
        System.out.println("0 - Sair");
        System.out.print("Escolha: ");
        return scanner.nextLine();
    }

    public String pedirTitulo() {
        System.out.print("Digite o título do livro: ");
        return scanner.nextLine();
    }

    public void mostrarLivros(List<Livro> livros) {
        System.out.println("\n=== LISTA DE LIVROS ===");

        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
            return;
        }

        for (Livro l : livros) {
            System.out.println(l.id() + " - " + l.titulo());
        }
    }

    public void mostrarMensagem(String msg) {
        System.out.println(msg);
    }
}
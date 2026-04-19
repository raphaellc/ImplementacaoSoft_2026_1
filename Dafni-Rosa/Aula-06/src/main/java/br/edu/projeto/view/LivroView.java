package br.edu.projeto.view;

import br.edu.projeto.model.Livro;

import java.util.List;
import java.util.Scanner;

public class LivroView {

    private final Scanner scanner = new Scanner(System.in);

    public int montarMenu() {
        System.out.println("\n╔══════════════════════════╗");
        System.out.println("║   Gerenciador de Livros  ║");
        System.out.println("╠══════════════════════════╣");
        System.out.println("║ 1. Adicionar livro       ║");
        System.out.println("║ 2. Listar livros         ║");
        System.out.println("║ 3. Marcar como lido      ║");
        System.out.println("║ 0. Sair                  ║");
        System.out.println("╚══════════════════════════╝");
        System.out.print("Escolha: ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String[] adicionarLivro() {
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        System.out.print("Autor: ");
        String autor = scanner.nextLine();
        return new String[]{titulo, autor};
    }

    public int marcarLivroComoLido() {
        System.out.print("ID do livro lido: ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void listarLivros(List<Livro> livros) {
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
            return;
        }
        System.out.println("\n--- Sua Lista de Livros ---");
        for (Livro l : livros) {
            String status = l.lido() ? "[✓ Lido]" : "[  Pendente]";
            System.out.printf("  #%d %s \"%s\" — %s%n", l.id(), status, l.titulo(), l.autor());
        }
    }

    public void exibirMensagem(String mensagem) {
        System.out.println(mensagem);
    }
}
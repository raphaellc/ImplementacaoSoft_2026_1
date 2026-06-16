package br.edu.projeto.view;

import java.util.List;
import java.util.Scanner;

import br.edu.projeto.model.Livro;

public class LivroView {
    private final Scanner scanner = new Scanner(System.in);

    public String exibirMenu() {
        System.out.println("\nGERENCIADOR DE LIVROS");
        System.out.println("1: Adicionar");
        System.out.println("2: Listar");
        System.out.println("0: Sair");
        System.out.print("Opção: ");
        return scanner.nextLine();
    }

    public void mostrarLivros(List<Livro> lista) {
        if (lista.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
        } else {
            System.out.println("\nLISTAGEM:");
            for (Livro livro : lista) {
                System.out.println("- " + livro.id() + ": " + livro.titulo() + " (" + livro.autor() + ")");
            }
        }
    }

    public String pedirTitulo() {
        System.out.print("Título do Livro: ");
        return scanner.nextLine();
    }

    public String pedirAutor() {
        System.out.print("Autor do Livro: ");
        return scanner.nextLine();
    }

    public void mensagem(String texto) {
        System.out.println(texto);
    }
}
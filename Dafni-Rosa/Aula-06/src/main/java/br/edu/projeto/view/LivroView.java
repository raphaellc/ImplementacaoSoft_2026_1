package br.edu.projeto.view;

import br.edu.projeto.model.Livro;

import java.util.List;
import java.util.Scanner;

public class LivroView {
    private final Scanner scanner;

    public LivroView() {
        this.scanner = new Scanner(System.in);
    }

    public int montarMenu() {
        System.out.println("\n========== Gerenciador de Livros ==========");
        System.out.println("0. Sair");
        System.out.println("1. Adicionar livro");
        System.out.println("2. Listar livros");
        System.out.println("3. Atualizar livro");
        System.out.println("4. Remover livro");
        System.out.println("===========================================");
        System.out.print("Escolha uma opção: ");
        return lerInteiro();
    }

    /**
     * Solicita e valida os dados para um novo livro.
     * Retorna um array: [titulo, autor].
     * Validação ocorre na View, conforme diagrama de sequência.
     */
    public String[] solicitarDadosLivro() {
        String titulo, autor;

        while (true) {
            System.out.print("Título: ");
            titulo = scanner.nextLine().trim();
            System.out.print("Autor: ");
            autor = scanner.nextLine().trim();

            String erroValidacao = validarDados(titulo, autor);
            if (erroValidacao == null) {
                break;
            }
            System.out.println("[Erro de validação] " + erroValidacao + " Tente novamente.\n");
        }

        return new String[]{titulo, autor};
    }

    /**
     * Valida os campos do livro. Retorna mensagem de erro ou null se válido.
     * Validação realizada na View conforme diagrama de sequência.
     */
    private String validarDados(String titulo, String autor) {
        if (titulo == null || titulo.isBlank()) return "Título não pode ser vazio.";
        if (autor == null || autor.isBlank())   return "Autor não pode ser vazio.";
        return null;
    }

    public int solicitarId(String acao) {
        System.out.print("Digite o ID do livro para " + acao + ": ");
        return lerInteiro();
    }

    /**
     * Solicita os campos a atualizar para um livro existente.
     * Campos deixados em branco mantêm o valor atual.
     */
    public String[] solicitarAtualizacoes(Livro livroAtual) {
        System.out.println("Deixe em branco para manter o valor atual.");

        System.out.print("Novo título [" + livroAtual.titulo() + "]: ");
        String titulo = scanner.nextLine().trim();

        System.out.print("Novo autor [" + livroAtual.autor() + "]: ");
        String autor = scanner.nextLine().trim();

        System.out.print("Disponível? (s/n) [" + (livroAtual.disponivel() ? "s" : "n") + "]: ");
        String dispInput = scanner.nextLine().trim().toLowerCase();

        // Aplica valor atual se campo deixado em branco
        String novoTitulo     = titulo.isBlank()    ? livroAtual.titulo()                   : titulo;
        String novoAutor      = autor.isBlank()     ? livroAtual.autor()                    : autor;
        String novoDisponivel = dispInput.isBlank() ? (livroAtual.disponivel() ? "s" : "n") : dispInput;

        return new String[]{novoTitulo, novoAutor, novoDisponivel};
    }

    public void listarLivros(List<Livro> livros) {
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
            return;
        }
        System.out.println("\n--- Lista de Livros ---");
        for (Livro livro : livros) {
            System.out.println(livro);
        }
        System.out.println("----------------------");
    }

    public void exibirMensagem(String mensagem) {
        System.out.println(mensagem);
    }

    // Lê inteiro com tratamento de erro para entradas inválidas
    private int lerInteiro() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Digite um número: ");
            }
        }
    }
}
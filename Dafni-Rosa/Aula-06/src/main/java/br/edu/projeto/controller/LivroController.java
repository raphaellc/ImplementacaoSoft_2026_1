package br.edu.projeto.controller;

import br.edu.projeto.model.Livro;
import br.edu.projeto.service.LivroService;
import br.edu.projeto.view.LivroView;

import java.util.List;
import java.util.Optional;

public class LivroController {

    private final LivroView view;
    private final LivroService service;

    public LivroController(LivroView view, LivroService service) {
        this.view = view;
        this.service = service;
    }

    public void iniciarGerenciador() {
        int opcao;
        do {
            opcao = view.montarMenu();
            switch (opcao) {
                case 1 -> adicionarLivro();
                case 2 -> listarLivros();
                case 3 -> marcarComoLido();
                case 0 -> view.exibirMensagem("Até logo!");
                default -> view.exibirMensagem("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void adicionarLivro() {
        String[] dados = view.adicionarLivro();
        try {
            service.adicionarLivro(dados[0], dados[1]);
            listarLivros();
        } catch (IllegalArgumentException e) {
            view.exibirMensagem("Erro: " + e.getMessage());
        }
    }

    private void listarLivros() {
        List<Livro> livros = service.listarLivros();
        view.listarLivros(livros);
    }

    private void marcarComoLido() {
        int id = view.marcarLivroComoLido();
        Optional<Livro> resultado = service.marcarLivroComoLido(id);
        if (resultado.isPresent()) {
            view.exibirMensagem("Livro #" + id + " marcado como lido!");
        } else {
            view.exibirMensagem("Livro com ID " + id + " não encontrado.");
        }
    }
}
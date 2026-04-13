package br.edu.projeto.controller;

import br.edu.projeto.model.Livro;
import br.edu.projeto.service.LivroService;
import br.edu.projeto.view.LivroView;

import java.util.List;

/**
 * Orquestra LivroView e LivroService.
 * Espelha a estrutura de TarefaController.
 */
public class LivroController {

    private final LivroView livroView;
    private final LivroService livroService;

    public LivroController(LivroView view, LivroService service) {
        this.livroView = view;
        this.livroService = service;
    }

    public void iniciarGerenciadorLivros() {
        int opcao;
        do {
            opcao = livroView.montarMenu();
            switch (opcao) {
                case 1 -> adicionarLivro();
                case 2 -> listarLivros();
                case 3 -> atualizarLivro();
                case 4 -> alterarDisponibilidade();
                case 5 -> removerLivro();
                case 0 -> livroView.exibirMensagem("Saindo do sistema.");
                default -> livroView.exibirMensagem("Opção inválida. Tente novamente.");
            }
        } while (opcao != 0);
    }

    public void adicionarLivro() {
        String[] dados = livroView.solicitarDadosLivro();
        try {
            int idGerado = livroService.adicionarLivro(dados[0], dados[1], dados[2]);
            if (idGerado > 0) {
                livroView.exibirMensagem("Livro adicionado com sucesso! ID: " + idGerado);
                listarLivros();
            } else {
                livroView.exibirMensagem("Erro ao adicionar o livro.");
            }
        } catch (IllegalArgumentException e) {
            livroView.exibirMensagem("Erro de validação: " + e.getMessage());
        }
    }

    public void listarLivros() {
        List<Livro> livros = livroService.listarLivros();
        livroView.listarLivros(livros);
    }

    public void atualizarLivro() {
        int id = livroView.solicitarId("atualizar");

        // Buscar livro atual para exibir valores na View
        livroService.listarLivros().stream()
                .filter(l -> l.id() == id)
                .findFirst()
                .ifPresentOrElse(livroAtual -> {
                    String[] alteracoes = livroView.solicitarAtualizacoes(livroAtual);
                    boolean disponivel = alteracoes[3].equals("s");
                    String mensagem = livroService.atualizarLivro(id, alteracoes[0], alteracoes[1], alteracoes[2], disponivel);
                    livroView.exibirMensagem(mensagem);
                    if (mensagem.contains("sucesso")) listarLivros();
                }, () -> livroView.exibirMensagem("Livro com ID " + id + " não encontrado."));
    }

    public void alterarDisponibilidade() {
        int id = livroView.solicitarId("alterar disponibilidade");
        String resp = livroView.solicitarDisponibilidade();
        boolean disponivel = resp.equals("s");
        String mensagem = livroService.alterarDisponibilidade(id, disponivel);
        livroView.exibirMensagem(mensagem);
        if (mensagem.contains("marcado")) listarLivros();
    }

    public void removerLivro() {
        int id = livroView.solicitarId("remover");
        String mensagem = livroService.removerLivro(id);
        livroView.exibirMensagem(mensagem);
        if (mensagem.contains("sucesso")) listarLivros();
    }
}
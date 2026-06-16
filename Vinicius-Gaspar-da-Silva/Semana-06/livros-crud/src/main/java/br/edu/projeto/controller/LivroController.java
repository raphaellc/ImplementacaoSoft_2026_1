package br.edu.projeto.controller;

import br.edu.projeto.model.LivroRepository;
import br.edu.projeto.view.LivroView;

public class LivroController {
    private final LivroRepository repository;
    private final LivroView view;

    public LivroController(LivroRepository repository, LivroView view) {
        this.repository = repository;
        this.view = view;
    }

    public void iniciar() {
        boolean continuar = true;
        while (continuar) {
            String opcao = view.exibirMenu();

            switch (opcao) {
                case "1":
                    String titulo = view.pedirTitulo();
                    String autor = view.pedirAutor();
                    repository.adicionarLivro(titulo, autor);
                    view.mensagem("Livro adicionado com sucesso!");
                    break;
                case "2":
                    view.mostrarLivros(repository.listarLivros());
                    break;
                case "0":
                    view.mensagem("Saindo do sistema...");
                    continuar = false;
                    break;
                default:
                    view.mensagem("Opção inválida!");
                    break;
            }
        }
    }
}
package br.edu.projeto.controller;

import br.edu.projeto.service.LivroService;
import br.edu.projeto.view.LivroView;

public class LivroController {
    private final LivroView view;
    private final LivroService livro_service;

    public LivroController(LivroView view, LivroService service) {
        this.view = view;
        this.livro_service = service;
    }

    public void iniciar() {
        boolean continuar = true;
        while (continuar) {
            String opcao = view.exibirMenu();

            switch (opcao) {
                case "1":
                    adicionarLivro();
                    break;
                case "2":
                    listarLivros();
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

    public void adicionarLivro() {
        String titulo = view.pedirTitulo();
        String autor = view.pedirAutor();
        livro_service.adicionarLivro(titulo, autor);
        view.mensagem("Livro adicionado com sucesso!");
    }
    
    public void listarLivros() {
        view.mostrarLivros(livro_service.listarLivros());
    }
}
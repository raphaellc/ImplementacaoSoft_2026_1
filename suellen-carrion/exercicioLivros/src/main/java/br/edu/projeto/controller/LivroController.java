package br.edu.projeto.controller;

import br.edu.projeto.service.LivroService;
import br.edu.projeto.view.LivroView;

public class LivroController {

    private LivroService service;
    private LivroView view;

    public LivroController(LivroService service, LivroView view) {
        this.service = service;
        this.view = view;
    }

    public void iniciar() {
        boolean executando = true;

        while (executando) {
            String opcao = view.mostrarMenu();

            switch (opcao) {
                case "1" -> {
                    String titulo = view.pedirTitulo();
                    service.adicionarLivro(titulo);
                    view.mostrarMensagem("Livro adicionado!");
                }

                case "2" -> {
                    view.mostrarLivros(service.listarLivros());
                }

                case "0" -> {
                    executando = false;
                    view.mostrarMensagem("Saindo...");
                }

                default -> view.mostrarMensagem("Opção inválida!");
            }
        }
    }
}
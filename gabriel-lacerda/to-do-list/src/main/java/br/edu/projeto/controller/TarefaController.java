package br.edu.projeto.controller;

import br.edu.projeto.model.TarefaRepositorio;
import br.edu.projeto.view.TarefaView;

public class TarefaController {

    private final TarefaRepositorio repositorio;
    private final TarefaView view;

    public TarefaController(TarefaRepositorio repositorio, TarefaView view) {
        this.repositorio = repositorio;
        this.view = view;
    }

    public void iniciar() {
        boolean rodando = true;
        while (rodando) {
            int opcao = view.exibirMenu();
            switch (opcao) {
                case 1 -> {
                    String descricao = view.lerDescricao();
                    repositorio.adicionar(descricao);
                    view.exibirMensagem("Tarefa adicionada.");
                }
                case 2 -> view.exibirTarefas(repositorio.listarTodas());
                case 3 -> {
                    int id = view.lerId();
                    boolean ok = repositorio.concluir(id);
                    view.exibirMensagem(ok ? "Tarefa concluida." : "ID nao encontrado.");
                }
                case 0 -> rodando = false;
                default -> view.exibirMensagem("Opcao invalida.");
            }
        }
    }
}
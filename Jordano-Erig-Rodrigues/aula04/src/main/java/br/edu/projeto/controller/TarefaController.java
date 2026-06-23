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
            view.exibirMenu();
            String opcao = view.lerOpcao();

            switch (opcao) {
                case "1" -> {
                    String descricao = view.lerDescricao();
                    repositorio.adicionar(descricao);
                    view.exibirMensagem("Tarefa adicionada com sucesso!");
                }
                case "2" -> view.exibirTarefas(repositorio.listarTodas());
                case "3" -> {
                    view.exibirTarefas(repositorio.listarTodas());
                    int id = view.lerId();
                    boolean sucesso = repositorio.concluir(id);
                    view.exibirMensagem(sucesso
                            ? "Tarefa concluída com sucesso!"
                            : "Tarefa não encontrada.");
                }
                case "0" -> {
                    view.exibirMensagem("Até logo!");
                    rodando = false;
                }
                default -> view.exibirMensagem("Opção inválida. Tente novamente.");
            }
        }
    }
}
package br.edu.projeto.controller;

import br.edu.projeto.model.TarefaService;
import br.edu.projeto.view.TarefaView;

public class TarefaController {

    private TarefaService service;
    private TarefaView view;

    // injeção de dependência
    public TarefaController(TarefaService service, TarefaView view) {
        this.service = service;
        this.view = view;
    }

    public void iniciar() {
        boolean executando = true;

        while (executando) {
            String opcao = view.mostrarMenu();

            switch (opcao) {
                case "1" -> {
                    String descricao = view.pedirDescricao();
                    service.adicionarTarefa(descricao);
                    view.mostrarMensagem("Tarefa adicionada!");
                }

                case "2" -> {
                    view.mostrarTarefas(service.listarTarefas());
                }

                case "3" -> {
                    int id = view.pedirId();
                    service.concluirTarefa(id);
                    view.mostrarMensagem("Tarefa atualizada!");
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
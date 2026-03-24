package br.edu.projeto.controller;

import br.edu.projeto.model.Tarefa;
import br.edu.projeto.model.TarefaRepository;
import br.edu.projeto.view.TarefaView;

public class TarefaController {

    private final TarefaRepository repository;
    private final TarefaView view;

    public TarefaController(TarefaRepository repository, TarefaView view) {
        this.repository = repository;
        this.view = view;
    }

    public void iniciar() {
        while (true) {
            String opcao = view.mostrarMenu();

            switch (opcao) {
                case "1" -> adicionarTarefa();
                case "2" -> listarTarefas();
                case "3" -> concluirTarefa();
                case "0" -> {
                    view.mostrarMensagem("Encerrando...");
                    return;
                }
                default -> view.mostrarMensagem("Opção inválida!");
            }
        }
    }

    private void adicionarTarefa() {
        String descricao = view.lerDescricao();
        repository.adicionar(descricao);
        view.mostrarMensagem("Tarefa adicionada!");
    }

    private void listarTarefas() {
        view.mostrarTarefas(repository.listar());
    }

    private void concluirTarefa() {
        int id = view.lerId();

        repository.buscarPorId(id).ifPresentOrElse(
                tarefa -> {
                    Tarefa atualizada = tarefa.marcarComoConcluida();
                    repository.atualizar(atualizada);
                    view.mostrarMensagem("Tarefa concluída!");
                },
                () -> view.mostrarMensagem("Tarefa não encontrada!")
        );
    }
}
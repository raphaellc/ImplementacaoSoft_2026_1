package Aula04.tarefa.src.main.java.com.artur.controller;

import Aula04.tarefa.src.main.java.com.artur.model.Tarefa;
import Aula04.tarefa.src.main.java.com.artur.view.TarefaView;

import java.util.ArrayList;
import java.util.List;

public class TarefaController {

    private List<Tarefa> tarefas = new ArrayList<>();
    private TarefaView view = new TarefaView();

    public void iniciar() {

        while (true) {

            int opcao = view.apresentarMenu();

            switch (opcao) {

                case 1:
                    String descricao = view.lerDescricaoTarefa();
                    adicionarTarefa(descricao);
                    break;

                case 2:
                    view.mostrarTarefas(listarTarefas());
                    break;

                case 3:
                    int id = view.lerIdTarefa();
                    concluirTarefa(id);
                    break;

                default:
                    view.mostrarMensagem("Opção inválida!");
            }
        }
    }

    public void adicionarTarefa(String descricao) {
        tarefas.add(new Tarefa(tarefas.size(), descricao, false));
    }

    public String listarTarefas() {
        return tarefas.toString();
    }

    public void concluirTarefa(int id) {

        for (int i = 0; i < tarefas.size(); i++) {

            Tarefa tarefa = tarefas.get(i);

            if (tarefa.id() == id) {
                tarefas.set(i, new Tarefa(tarefa.id(), tarefa.descricao(), true));
            }
        }
    }
}
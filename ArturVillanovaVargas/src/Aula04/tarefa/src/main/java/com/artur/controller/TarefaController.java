package Aula04.tarefa.src.main.java.com.artur.controller;

import Aula04.tarefa.src.main.java.com.artur.model.Tarefa;

import java.util.ArrayList;
import java.util.List;


public class TarefaController {
    private List<Tarefa> tarefas = new ArrayList<>();

    public void adicionarTarefa(String descricao) {
        tarefas.add(new Tarefa(tarefas.size(), descricao, false));
    }

    public String listarTarefas() {
        return tarefas.toString();
    }

    // record imutavel, ent tem que trocar por uma copia
    public void concluirTarefa(int id) {
        for (int i = 0; i < tarefas.size(); i++) {
            Tarefa tarefa = tarefas.get(i);

            if(tarefa.id() == id) {
                tarefas.set(i, new Tarefa(tarefa.id(), tarefa.descricao(), true));
            }
        }
    }
}

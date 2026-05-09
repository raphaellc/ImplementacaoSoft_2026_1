package br.edu.projeto.model;

import java.util.ArrayList;
import java.util.List;

public class TarefaService {

    private List<Tarefa> tarefas = new ArrayList<>();
    private int contadorId = 1;

    public void adicionarTarefa(String descricao) {
        Tarefa tarefa = new Tarefa(contadorId++, descricao, false);
        tarefas.add(tarefa);
    }

    public List<Tarefa> listarTarefas() {
        return tarefas;
    }

    public void concluirTarefa(int id) {
        for (int i = 0; i < tarefas.size(); i++) {
            Tarefa t = tarefas.get(i);
            if (t.id() == id) {
                tarefas.set(i, new Tarefa(t.id(), t.descricao(), true));
                return;
            }
        }
        System.out.println("Tarefa não encontrada.");
    }
}
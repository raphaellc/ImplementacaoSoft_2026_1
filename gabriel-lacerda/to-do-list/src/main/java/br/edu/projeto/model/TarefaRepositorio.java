package br.edu.projeto.model;

import java.util.ArrayList;
import java.util.List;

public class TarefaRepositorio {

    private final List<Tarefa> tarefas = new ArrayList<>();
    private int proximoId = 1;

    public void adicionar(String descricao) {
        tarefas.add(new Tarefa(proximoId++, descricao, false));
    }

    public List<Tarefa> listarTodas() {
        return List.copyOf(tarefas);
    }

    public boolean concluir(int id) {
        for (int i = 0; i < tarefas.size(); i++) {
            if (tarefas.get(i).id() == id) {
                tarefas.set(i, tarefas.get(i).comConcluida());
                return true;
            }
        }
        return false;
    }
}
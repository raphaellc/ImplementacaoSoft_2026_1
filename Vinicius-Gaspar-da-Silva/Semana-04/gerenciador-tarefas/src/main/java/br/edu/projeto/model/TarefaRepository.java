package br.edu.projeto.model;

import java.util.ArrayList;
import java.util.List;

public class TarefaRepository {
    private List<Tarefa> tarefas = new ArrayList<>();
    private int contadorId = 1;

    public void adicionarTarefa(String descricao) {
        tarefas.add(new Tarefa(contadorId++, descricao, false));
    }

    public List<Tarefa> listarTarefas() {
        return List.copyOf(tarefas); 
    }

    public boolean marcarComoConcluida(int id) {
        for (int i = 0; i < tarefas.size(); i++) {
            if (tarefas.get(i).id() == id) {
                tarefas.set(i, tarefas.get(i).tarefaComConcluida(true));
                return true;
            }
        }
        return false;
    }
}
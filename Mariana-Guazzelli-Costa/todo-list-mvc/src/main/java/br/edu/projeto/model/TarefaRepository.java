package br.edu.projeto.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TarefaRepository {

    private final List<Tarefa> tarefas = new ArrayList<>();
    private int proximoId = 1;

    public void adicionar(String descricao) {
        tarefas.add(new Tarefa(proximoId++, descricao, false));
    }

    public List<Tarefa> listar() {
        return tarefas;
    }

    public Optional<Tarefa> buscarPorId(int id) {
        return tarefas.stream()
                .filter(t -> t.id() == id)
                .findFirst();
    }

    public void atualizar(Tarefa tarefaAtualizada) {
        tarefas.removeIf(t -> t.id() == tarefaAtualizada.id());
        tarefas.add(tarefaAtualizada);
    }
}
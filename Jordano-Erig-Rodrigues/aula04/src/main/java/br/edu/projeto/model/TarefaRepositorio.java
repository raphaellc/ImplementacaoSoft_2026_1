package br.edu.projeto.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TarefaRepositorio {

    private final ArrayList<Tarefa> tarefas = new ArrayList<>();
    private int proximoId = 1;

    public void adicionar(String descricao) {
        tarefas.add(new Tarefa(proximoId++, descricao, false));
    }

    public List<Tarefa> listarTodas() {
        return List.copyOf(tarefas);
    }

    public Optional<Tarefa> buscarPorId(int id) {
        return tarefas.stream()
                .filter(t -> t.id() == id)
                .findFirst();
    }

    public boolean concluir(int id) {
        Optional<Tarefa> encontrada = buscarPorId(id);
        if (encontrada.isPresent()) {
            tarefas.replaceAll(t -> t.id() == id ? t.concluir() : t);
            return true;
        }
        return false;
    }
}
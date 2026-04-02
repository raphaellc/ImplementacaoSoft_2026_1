package br.edu.projeto.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Optional<Tarefa> encontrada = tarefas.stream()
                .filter(t -> t.id() == id)
                .findFirst();

        encontrada.ifPresent(t -> tarefas.set(tarefas.indexOf(t), t.comConcluida()));
        return encontrada.isPresent();
    }
}

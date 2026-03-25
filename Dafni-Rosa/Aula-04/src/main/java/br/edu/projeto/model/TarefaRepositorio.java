package br.edu.projeto.model;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
 
public class TarefaRepositorio {
 
    private final List<Tarefa> tarefas;
    private int proximoId;
 
    public TarefaRepositorio() {
        this.tarefas = new ArrayList<>();
        this.proximoId = 1;
    }
 
    public Tarefa adicionar(String descricao) {
        Tarefa novaTarefa = new Tarefa(proximoId++, descricao, false);
        tarefas.add(novaTarefa);
        return novaTarefa;
    }
 
    public List<Tarefa> listarTodas() {
        return Collections.unmodifiableList(tarefas);
    }
 
    public Optional<Tarefa> concluir(int id) {
        for (int i = 0; i < tarefas.size(); i++) {
            if (tarefas.get(i).id() == id) {
                Tarefa atualizada = tarefas.get(i).marcarComoConcluida();
                tarefas.set(i, atualizada);
                return Optional.of(atualizada);
            }
        }
        return Optional.empty();
    }
 
    public Optional<Tarefa> buscarPorId(int id) {
        return tarefas.stream()
                .filter(t -> t.id() == id)
                .findFirst();
    }
 
    public boolean estaVazia() {
        return tarefas.isEmpty();
    }
}
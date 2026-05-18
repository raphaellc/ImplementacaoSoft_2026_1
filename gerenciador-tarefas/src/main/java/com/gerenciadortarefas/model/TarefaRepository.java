package com.gerenciadortarefas.model;

import java.util.List;
import java.util.Optional;

public interface TarefaRepository {
    Tarefa adicionarTarefa(String descricao);
    boolean atualizarTarefa(Tarefa tarefaAtualizada);
    boolean deletarTarefa(int id);
    List<Tarefa> listarTarefas();
    Optional<Tarefa> buscarPorId(int id);
}

package com.gerenciadortarefas.model;

import java.util.List;
import java.util.Optional;

public interface TarefaRepository {
    Tarefa adicionarTarefa(int usuarioId, String descricao);
    boolean atualizarTarefa(Tarefa tarefaAtualizada);
    boolean deletarTarefa(int usuarioId, int id);
    List<Tarefa> listarTarefas(int usuarioId);
    Optional<Tarefa> buscarPorId(int usuarioId, int id);
}

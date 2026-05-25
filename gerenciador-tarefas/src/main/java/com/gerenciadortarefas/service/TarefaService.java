package com.gerenciadortarefas.service;

import com.gerenciadortarefas.model.Tarefa;
import java.util.List;
import java.util.Optional;

public interface TarefaService {
    Tarefa adicionarTarefa(int usuarioId, String descricao);
    List<Tarefa> listarTarefas(int usuarioId);
    Optional<Tarefa> marcarTarefaConcluida(int usuarioId, int id);
    Optional<Tarefa> atualizarTarefa(int usuarioId, int id, String descricao, Boolean concluida);
    boolean deletarTarefa(int usuarioId, int id);
}

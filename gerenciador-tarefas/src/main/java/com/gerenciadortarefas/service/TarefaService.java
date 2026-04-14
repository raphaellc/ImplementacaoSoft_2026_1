package com.gerenciadortarefas.service;


import com.gerenciadortarefas.model.Tarefa;
import java.util.List;
import java.util.Optional;

public interface TarefaService {
    Tarefa adicionarTarefa(String descricao);
    List<Tarefa> listarTarefas();
    Optional<Tarefa> marcarTarefaConcluida(int id);
}

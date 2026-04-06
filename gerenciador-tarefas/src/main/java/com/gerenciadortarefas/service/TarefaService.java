package com.gerenciadortarefas.service;


import com.gerenciadortarefas.model.Tarefa;
import java.util.List;

public interface TarefaService {
    void adicionarTarefa(String descricao);
    List<Tarefa> listarTarefas();
    String marcarTarefaConcluida(int id);
}

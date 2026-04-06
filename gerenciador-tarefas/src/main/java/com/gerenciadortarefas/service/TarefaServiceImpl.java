package com.gerenciadortarefas.service;

import com.gerenciadortarefas.model.Tarefa;
import com.gerenciadortarefas.model.TarefaRepository;
import java.util.List;
import java.util.Optional;

public class TarefaServiceImpl implements TarefaService {
    private TarefaRepository repository;

    public TarefaServiceImpl(TarefaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void adicionarTarefa(String descricao) {
        // Regra de negócio: não permitir descrições vazias
        if (descricao != null && !descricao.isBlank()) {
            repository.adicionarTarefa(descricao);
        }
    }

    @Override
    public List<Tarefa> listarTarefas() {
        return repository.listarTarefas();
    }

    @Override
    public String marcarTarefaConcluida(int id) {
        Optional<Tarefa> tarefaOptional = repository.buscarPorId(id);
        if (tarefaOptional.isPresent()) {
            Tarefa tarefa = tarefaOptional.get();
            Tarefa tarefaModificada = tarefa.comConcluida(true);
            if (repository.atualizarTarefa(tarefaModificada)) {
                return "Tarefa " + id + " marcada como concluída.";
            }
            return "Erro ao atualizar a tarefa.";
        }
        return "Tarefa com ID " + id + " não encontrada.";
    }
}

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
    public Tarefa adicionarTarefa(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("A descrição não pode ser vazia");
        }
        if (descricao.length() > 255) {
            throw new IllegalArgumentException("A descrição deve ter no máximo 255 caracteres");
        }
        return repository.adicionarTarefa(descricao.trim());
    }

    @Override
    public List<Tarefa> listarTarefas() {
        return repository.listarTarefas();
    }

    @Override
    public Optional<Tarefa> marcarTarefaConcluida(int id) {
        Optional<Tarefa> tarefaOptional = repository.buscarPorId(id);
        if (tarefaOptional.isPresent()) {
            Tarefa tarefaModificada = tarefaOptional.get().comConcluida(true);
            if (repository.atualizarTarefa(tarefaModificada)) {
                return Optional.of(tarefaModificada);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Tarefa> atualizarTarefa(int id, String descricao, Boolean concluida) {
        Optional<Tarefa> tarefaOptional = repository.buscarPorId(id);
        if (tarefaOptional.isEmpty()) {
            return Optional.empty();
        }
        Tarefa atual = tarefaOptional.get();
        String novaDescricao = (descricao != null && !descricao.isBlank()) ? descricao.trim() : atual.descricao();
        boolean novaConcluida = (concluida != null) ? concluida : atual.concluida();
        Tarefa tarefaAtualizada = new Tarefa(id, novaDescricao, novaConcluida);
        if (repository.atualizarTarefa(tarefaAtualizada)) {
            return Optional.of(tarefaAtualizada);
        }
        return Optional.empty();
    }

    @Override
    public boolean deletarTarefa(int id) {
        return repository.deletarTarefa(id);
    }
}

package com.gerenciadortarefas.service;

import com.gerenciadortarefas.model.Tarefa;
import com.gerenciadortarefas.model.TarefaRepository;
import java.util.List;
import java.util.Optional;

public class TarefaServiceImpl implements TarefaService {
    private final TarefaRepository repository;

    public TarefaServiceImpl(TarefaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Tarefa adicionarTarefa(int usuarioId, String descricao) {
        if (descricao == null || descricao.isBlank())
            throw new IllegalArgumentException("A descrição não pode ser vazia");
        if (descricao.length() > 255)
            throw new IllegalArgumentException("A descrição deve ter no máximo 255 caracteres");
        return repository.adicionarTarefa(usuarioId, descricao.trim());
    }

    @Override
    public List<Tarefa> listarTarefas(int usuarioId) {
        return repository.listarTarefas(usuarioId);
    }

    @Override
    public Optional<Tarefa> marcarTarefaConcluida(int usuarioId, int id) {
        return repository.buscarPorId(usuarioId, id)
                .map(t -> t.comConcluida(true))
                .filter(repository::atualizarTarefa);
    }

    @Override
    public Optional<Tarefa> atualizarTarefa(int usuarioId, int id, String descricao, Boolean concluida) {
        Optional<Tarefa> opt = repository.buscarPorId(usuarioId, id);
        if (opt.isEmpty()) return Optional.empty();
        Tarefa atual = opt.get();
        String novaDescricao = (descricao != null && !descricao.isBlank()) ? descricao.trim() : atual.descricao();
        boolean novaConcluida = (concluida != null) ? concluida : atual.concluida();
        Tarefa nova = new Tarefa(id, usuarioId, novaDescricao, novaConcluida);
        return repository.atualizarTarefa(nova) ? Optional.of(nova) : Optional.empty();
    }

    @Override
    public boolean deletarTarefa(int usuarioId, int id) {
        return repository.deletarTarefa(usuarioId, id);
    }
}

package com.gerenciadortarefas.auth.model;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SessaoRepository {
    void criar(Sessao sessao);
    Optional<Sessao> buscarPorTokenHash(String tokenHash);
    void atualizarAtividade(String tokenHash, LocalDateTime quando);
    void encerrar(String tokenHash, String motivo, LocalDateTime quando);
}

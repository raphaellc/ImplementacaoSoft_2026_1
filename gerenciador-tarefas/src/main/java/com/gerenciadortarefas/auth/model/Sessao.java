package com.gerenciadortarefas.auth.model;

import java.time.LocalDateTime;

/**
 * Sessão ativa de um usuário. {@code tokenHash} é o SHA-256 do token enviado
 * ao cliente — se o banco vazar, sessões não podem ser sequestradas.
 */
public record Sessao(
        String tokenHash,
        int usuarioId,
        LocalDateTime criadoEm,
        LocalDateTime lastActivity,
        String ipAddress,
        String userAgent,
        String csrfToken,
        LocalDateTime revogadaEm,
        String motivoLogout
) {
    public boolean ativa() {
        return revogadaEm == null;
    }
}

package com.gerenciadortarefas.auth.model;

import java.time.LocalDateTime;

/**
 * Challenge intermediário entre "senha OK" e "código TOTP OK".
 * TTL curto (default 5 min). Sem este token, um cliente com senha correta
 * poderia pular MFA chamando direto outro endpoint.
 */
public record MfaPending(
        String tokenHash,
        int usuarioId,
        LocalDateTime expiraEm
) {}

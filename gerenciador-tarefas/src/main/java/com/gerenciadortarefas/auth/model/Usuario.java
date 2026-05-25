package com.gerenciadortarefas.auth.model;

import java.time.LocalDateTime;

/**
 * Usuário do sistema. {@code otpSecretCifrado} guarda o segredo TOTP em AES-GCM
 * — nunca em texto puro. {@code mfaHabilitado=true} só após o primeiro código
 * ser confirmado (ver {@code AuthService.confirmarMfa}).
 */
public record Usuario(
        int id,
        String email,
        String username,
        String senhaHash,
        boolean mfaHabilitado,
        byte[] otpSecretCifrado,
        LocalDateTime ultimoLogin,
        LocalDateTime ultimoLogout
) {}

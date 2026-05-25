package com.gerenciadortarefas.auth.service;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * BCrypt cost 12 (~250ms por hash em hardware moderno).
 * Comparação em tempo constante embutida.
 */
public class PasswordHasher {

    private static final int COST = 12;

    public String hash(String senhaPura) {
        return BCrypt.withDefaults().hashToString(COST, senhaPura.toCharArray());
    }

    public boolean verificar(String senhaPura, String hash) {
        if (senhaPura == null || hash == null) return false;
        return BCrypt.verifyer().verify(senhaPura.toCharArray(), hash).verified;
    }
}

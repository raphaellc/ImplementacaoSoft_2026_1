package com.gerenciadortarefas.auth.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Gera tokens opacos de 256 bits via SecureRandom e calcula SHA-256
 * para armazenamento (princípio: nunca guardar o token em texto no DB).
 */
public class TokenService {

    private static final SecureRandom RNG = new SecureRandom();
    private static final Base64.Encoder ENC = Base64.getUrlEncoder().withoutPadding();

    /** Retorna o token em texto puro — enviar ao cliente, NÃO persistir. */
    public String gerarToken() {
        byte[] bytes = new byte[32];
        RNG.nextBytes(bytes);
        return ENC.encodeToString(bytes);
    }

    /** Hash SHA-256 (hex de 64 chars) — persistir esse valor no DB. */
    public String hash(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível", e);
        }
    }
}

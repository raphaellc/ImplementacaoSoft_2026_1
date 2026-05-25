package com.gerenciadortarefas.auth.service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-GCM para cifrar dados sensíveis em repouso (otp_secret).
 * KEK lida da env {@code AUTH_KEK} em Base64 (32 bytes = AES-256).
 * Se {@code AUTH_KEK} não estiver definida, deriva uma chave volátil
 * e avisa no log — adequado apenas para desenvolvimento.
 */
public class CryptoService {

    private static final int IV_LEN_BYTES   = 12;
    private static final int TAG_LEN_BITS   = 128;
    private static final SecureRandom RNG   = new SecureRandom();

    private final SecretKeySpec key;

    public CryptoService() {
        String b64 = System.getenv("AUTH_KEK");
        byte[] keyBytes;
        if (b64 == null || b64.isBlank()) {
            System.out.println("[CryptoService] AVISO: AUTH_KEK não definida — gerando chave volátil. Segredos MFA não sobreviverão a reinicializações.");
            keyBytes = new byte[32];
            RNG.nextBytes(keyBytes);
        } else {
            keyBytes = Base64.getDecoder().decode(b64);
            if (keyBytes.length != 32) {
                throw new IllegalStateException("AUTH_KEK deve ter 32 bytes (Base64 de 32 bytes = 44 chars)");
            }
        }
        this.key = new SecretKeySpec(keyBytes, "AES");
    }

    /** Cifra texto UTF-8 → [iv (12B)] [ciphertext+tag]. */
    public byte[] cifrar(String plaintext) {
        try {
            byte[] iv = new byte[IV_LEN_BYTES];
            RNG.nextBytes(iv);
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LEN_BITS, iv));
            byte[] ct = c.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return ByteBuffer.allocate(iv.length + ct.length).put(iv).put(ct).array();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao cifrar", e);
        }
    }

    public String decifrar(byte[] envelope) {
        try {
            ByteBuffer bb = ByteBuffer.wrap(envelope);
            byte[] iv = new byte[IV_LEN_BYTES];
            bb.get(iv);
            byte[] ct = new byte[bb.remaining()];
            bb.get(ct);
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LEN_BITS, iv));
            return new String(c.doFinal(ct), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao decifrar", e);
        }
    }
}

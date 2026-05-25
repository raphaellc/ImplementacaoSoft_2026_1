package com.gerenciadortarefas.auth.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Janela deslizante em memória — adequado para single-instance.
 * Em produção multi-instance: migrar para Redis ou tabela com índices.
 */
public class RateLimiter {

    private final int maxTentativas;
    private final Duration janela;
    private final Map<String, Deque<Instant>> historico = new ConcurrentHashMap<>();

    public RateLimiter(int maxTentativas, Duration janela) {
        this.maxTentativas = maxTentativas;
        this.janela = janela;
    }

    /** Retorna true se a tentativa é permitida (e a registra). */
    public boolean permitir(String chave) {
        Instant agora = Instant.now();
        Instant limite = agora.minus(janela);
        Deque<Instant> fila = historico.computeIfAbsent(chave, k -> new ArrayDeque<>());
        synchronized (fila) {
            while (!fila.isEmpty() && fila.peekFirst().isBefore(limite)) {
                fila.pollFirst();
            }
            if (fila.size() >= maxTentativas) {
                return false;
            }
            fila.addLast(agora);
            return true;
        }
    }

    public void limpar(String chave) {
        historico.remove(chave);
    }
}

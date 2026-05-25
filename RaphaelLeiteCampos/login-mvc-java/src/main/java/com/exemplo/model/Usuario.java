package com.exemplo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model: Representa os dados do usuário e a lógica de persistência simulada.
 * No Java 21+, records são ideais para representar dados imutáveis.
 */
public record Usuario(String email, String senha) {
    
    // Simulação de um banco de dados em memória para a aula 1
    private static final List<Usuario> BANCO_DADOS = new ArrayList<>();

    static {
        // Usuário de teste inicial
        BANCO_DADOS.add(new Usuario("aluno@escola.com", "senha123"));
        BANCO_DADOS.add(new Usuario("professor@escola.com", "admin456"));
    }

    /**
     * Lógica de negócio: Valida se as credenciais existem no "banco".
     */
    public static boolean validar(String email, String senha) {
        return BANCO_DADOS.stream()
                .anyMatch(u -> u.email().equalsIgnoreCase(email) && u.senha().equals(senha));
    }
}

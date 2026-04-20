package com.login.model;

/**
 * DATA TRANSFER OBJECTS (DTOs)
 * ============================
 * São objetos usados para transferir dados entre camadas,
 * especialmente entre Controller e o mundo externo (JSON).
 *
 * Separar DTO do Model evita expor dados internos (ex: senhaHash)
 * e permite que a API evolua sem impactar o modelo de domínio.
 */
public class LoginRequest {

    private String email;
    private String senha;

    // Construtor padrão — necessário para desserialização JSON
    public LoginRequest() {}

    public LoginRequest(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    public String getEmail() { return email; }
    public String getSenha() { return senha; }

    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }
}

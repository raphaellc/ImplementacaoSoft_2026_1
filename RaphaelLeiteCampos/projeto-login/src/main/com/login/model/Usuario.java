package com.login.model;

/**
 * CAMADA MODEL
 * ============
 * Representa os dados de um usuário no sistema.
 * Não contém lógica de negócio — apenas estrutura os dados.
 *
 * Boas práticas aplicadas:
 *  - Encapsulamento com private + getters/setters
 *  - Construtores explícitos
 *  - toString() para depuração
 */
public class Usuario {

    private Long id;
    private String email;
    private String senhaHash;   // Nunca armazenamos senha em texto puro!
    private String nome;
    private boolean ativo;

    // Construtor para criar novo usuário (sem id — gerado pelo banco)
    public Usuario(String nome, String email, String senhaHash) {
        this.nome      = nome;
        this.email     = email;
        this.senhaHash = senhaHash;
        this.ativo     = true;
    }

    // Construtor completo — usado ao recuperar do banco
    public Usuario(Long id, String nome, String email, String senhaHash, boolean ativo) {
        this.id        = id;
        this.nome      = nome;
        this.email     = email;
        this.senhaHash = senhaHash;
        this.ativo     = ativo;
    }

    // ───── Getters ─────
    public Long    getId()        { return id; }
    public String  getNome()      { return nome; }
    public String  getEmail()     { return email; }
    public String  getSenhaHash() { return senhaHash; }
    public boolean isAtivo()      { return ativo; }

    // ───── Setters ─────
    public void setId(Long id)           { this.id = id; }
    public void setNome(String nome)     { this.nome = nome; }
    public void setEmail(String email)   { this.email = email; }
    public void setAtivo(boolean ativo)  { this.ativo = ativo; }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nome='" + nome + "', email='" + email + "', ativo=" + ativo + "}";
    }
}

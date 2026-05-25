package com.login.model;

public class CadastroRequest {
    private String nome;
    private String email;
    private String senha;

    public CadastroRequest() {}

    public String getNome()  { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }

    public void setNome(String nome)   { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }
}

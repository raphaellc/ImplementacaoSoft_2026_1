package com.login.model;

/**
 * Resposta padronizada da API de login.
 * Nunca retorna senhaHash — apenas dados seguros para o front.
 */
public class LoginResponse {

    private boolean sucesso;
    private String  mensagem;
    private String  nomeUsuario;  // Só presente se sucesso=true
    private String  token;        // Token simples de sessão

    // Construtor para sucesso
    public static LoginResponse sucesso(String nomeUsuario, String token) {
        LoginResponse r = new LoginResponse();
        r.sucesso      = true;
        r.mensagem     = "Login realizado com sucesso!";
        r.nomeUsuario  = nomeUsuario;
        r.token        = token;
        return r;
    }

    // Construtor para falha
    public static LoginResponse falha(String motivo) {
        LoginResponse r = new LoginResponse();
        r.sucesso  = false;
        r.mensagem = motivo;
        return r;
    }

    // Getters
    public boolean isSucesso()       { return sucesso; }
    public String  getMensagem()     { return mensagem; }
    public String  getNomeUsuario()  { return nomeUsuario; }
    public String  getToken()        { return token; }
}

package com.login.controller;

import com.login.model.CadastroRequest;
import com.login.model.LoginRequest;
import com.login.model.LoginResponse;
import com.login.server.ServidorHttp;
import com.login.service.AuthService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * CAMADA CONTROLLER
 * =================
 * Responsabilidade: receber requisições HTTP, extrair os dados,
 * chamar o Service e devolver a resposta no formato correto (JSON).
 *
 * O Controller NÃO tem regras de negócio.
 * Perguntas que o Controller responde:
 *  - Qual método HTTP é esperado? (POST)
 *  - Qual o formato da entrada? (JSON no corpo)
 *  - Qual status HTTP devolver? (200, 400, 405...)
 *  - Como serializar a resposta?
 *
 * O Controller delega TODO o trabalho real ao Service.
 */
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/login
     * Body: { "email": "...", "senha": "..." }
     */
    public void login(HttpExchange exchange) throws IOException {
        // Só aceita POST
        if (!"POST".equals(exchange.getRequestMethod())) {
            ServidorHttp.responder(exchange, 405,
                    ServidorHttp.GSON.toJson(new ServidorHttp.ErroResponse("Método não permitido. Use POST.")));
            return;
        }

        // Lê e desserializa o JSON do corpo
        String corpo = ServidorHttp.lerCorpo(exchange);
        LoginRequest request = ServidorHttp.GSON.fromJson(corpo, LoginRequest.class);

        // Chama o Service — toda a lógica está lá
        LoginResponse resposta = authService.autenticar(request);

        // Retorna 200 se sucesso, 401 se credenciais inválidas
        int statusHttp = resposta.isSucesso() ? 200 : 401;
        ServidorHttp.responder(exchange, statusHttp, ServidorHttp.GSON.toJson(resposta));
    }

    /**
     * POST /api/cadastro
     * Body: { "nome": "...", "email": "...", "senha": "..." }
     */
    public void cadastro(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            ServidorHttp.responder(exchange, 405,
                    ServidorHttp.GSON.toJson(new ServidorHttp.ErroResponse("Método não permitido. Use POST.")));
            return;
        }

        String corpo = ServidorHttp.lerCorpo(exchange);
        CadastroRequest request = ServidorHttp.GSON.fromJson(corpo, CadastroRequest.class);

        LoginResponse resposta = authService.cadastrar(
                request.getNome(),
                request.getEmail(),
                request.getSenha()
        );

        // 201 Created para cadastro bem-sucedido, 400 Bad Request para validação
        int statusHttp = resposta.isSucesso() ? 201 : 400;
        ServidorHttp.responder(exchange, statusHttp, ServidorHttp.GSON.toJson(resposta));
    }
}

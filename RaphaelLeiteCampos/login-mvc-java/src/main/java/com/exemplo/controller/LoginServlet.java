package com.exemplo.controller;

import com.exemplo.model.Usuario;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller: Atua como o intermediário entre a View (HTML/JS) e o Model (Usuario).
 * Implementa uma API REST simples para autenticação.
 */
@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Configura o tipo de resposta como JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        Map<String, Object> resultado = new HashMap<>();

        try {
            // 1. Receber e ler o JSON do corpo da requisição
            JsonNode jsonNode = objectMapper.readTree(request.getReader());
            String email = jsonNode.get("email").asText();
            String senha = jsonNode.get("senha").asText();

            // 2. Chamar a lógica do Model para validação
            boolean autenticado = Usuario.validar(email, senha);

            // 3. Preparar a resposta com base no resultado
            if (autenticado) {
                response.setStatus(HttpServletResponse.SC_OK);
                resultado.put("sucesso", true);
                resultado.put("mensagem", "Login realizado com sucesso!");
                resultado.put("usuario", email);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resultado.put("sucesso", false);
                resultado.put("mensagem", "E-mail ou senha incorretos.");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("sucesso", false);
            resultado.put("mensagem", "Erro ao processar a requisição: " + e.getMessage());
        }

        // 4. Enviar o JSON de resposta para a View
        out.print(objectMapper.writeValueAsString(resultado));
        out.flush();
    }
}

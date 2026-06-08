package com.gerenciadortarefas.api;

import com.gerenciadortarefas.GerenciadorTarefas;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TarefaApiE2ETest {

    private static HttpClient client;
    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    static void setUpAll() throws InterruptedException {
        // Inicializa o cliente primeiro para podermos usar no ping
        client = HttpClient.newBuilder().build();

        // Inicia o servidor real em uma thread separada para os testes
        Thread serverThread = new Thread(() -> GerenciadorTarefas.main(new String[]{}));
        serverThread.start();
        
        // Aguarda o servidor subir DINAMICAMENTE (Polling)
        boolean pronto = false;
        int tentativas = 0;
        
        while (!pronto && tentativas < 10) { // Tenta até 10 vezes (máximo 5 segundos)
            try {
                HttpRequest ping = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/"))
                        .GET()
                        .build();
                // Se o servidor responder (mesmo com 404 ou 200), significa que a porta está aberta
                client.send(ping, HttpResponse.BodyHandlers.ofString());
                pronto = true; 
            } catch (Exception e) {
                tentativas++;
                Thread.sleep(500); // Aguarda 500ms antes de tentar novamente
            }
        }

        if (!pronto) {
            throw new IllegalStateException("O servidor falhou ao subir na porta 8080 dentro do tempo limite.");
        }
        
        // Setup: Registrar um usuário de teste
        String jsonRegistro = "{\"email\":\"teste@e2e.com\", \"username\":\"testee2e\", \"senha\":\"Senha123\"}";
        HttpRequest reqRegistro = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRegistro))
                .build();
        try { client.send(reqRegistro, HttpResponse.BodyHandlers.ofString()); } catch (Exception e) {}
    }
    @Test
    @DisplayName("TE-01: Deve criar tarefa via API após realizar login com sucesso")
    void deveCriarTarefaE2E() throws Exception {
        // 1. Realizar Login para pegar o Token de Sessão e o CSRF
        String jsonLogin = "{\"login\":\"testee2e\", \"senha\":\"Senha123\"}";
        HttpRequest reqLogin = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonLogin))
                .build();
                
        HttpResponse<String> resLogin = client.send(reqLogin, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resLogin.statusCode(), "Login deve retornar 200 OK");
        
        // Extrai o Cookie de sessão e o token CSRF da resposta manual/didaticamente
        String setCookieHeader = resLogin.headers().firstValue("Set-Cookie").orElse("");
        String sessionCookie = setCookieHeader.split(";")[0]; // Pega apenas "sessao=valor"
        
        // Extrai o CSRF do corpo da resposta JSON (usando regex simples para evitar parser complexo no exemplo)
        String bodyLogin = resLogin.body();
        String csrfToken = bodyLogin.split("\"csrfToken\":\"")[1].split("\"")[0];

        // 2. Criar a Tarefa (POST /api/tarefas)
        String jsonNovaTarefa = "{\"descricao\":\"Tarefa criada pelo teste E2E\"}";
        HttpRequest reqCriar = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/tarefas"))
                .header("Content-Type", "application/json")
                .header("Cookie", sessionCookie)          // Passa o cookie obrigatório
                .header("X-CSRF-Token", csrfToken)        // Passa o header CSRF obrigatório
                .POST(HttpRequest.BodyPublishers.ofString(jsonNovaTarefa))
                .build();

        HttpResponse<String> resCriar = client.send(reqCriar, HttpResponse.BodyHandlers.ofString());
        
        // Verificações
        assertEquals(201, resCriar.statusCode(), "Deve retornar 201 Created");
        assertTrue(resCriar.body().contains("\"descricao\":\"Tarefa criada pelo teste E2E\""), 
                  "O corpo da resposta deve conter a descrição da tarefa");
    }

    @Test
    @DisplayName("TE-02: Deve bloquear criação de tarefa sem autenticação (Filtro intercepta)")
    void deveFalharSemAutenticacao() throws Exception {
        String jsonNovaTarefa = "{\"descricao\":\"Tentativa invasora\"}";
        
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/tarefas"))
                .header("Content-Type", "application/json")
                // Sem Cookie e sem CSRF
                .POST(HttpRequest.BodyPublishers.ofString(jsonNovaTarefa))
                .build();

        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        
        // SessionFilter deve retornar 401
        assertEquals(401, res.statusCode(), "A API deve proteger a rota e retornar 401");
    }
}
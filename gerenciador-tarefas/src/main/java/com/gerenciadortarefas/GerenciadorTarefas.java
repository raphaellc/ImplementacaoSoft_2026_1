package com.gerenciadortarefas;

import com.gerenciadortarefas.api.StaticHandler;
import com.gerenciadortarefas.api.TarefaHandler;
import com.gerenciadortarefas.auth.api.AuthHandler;
import com.gerenciadortarefas.auth.filter.CsrfFilter;
import com.gerenciadortarefas.auth.filter.SessionFilter;
import com.gerenciadortarefas.auth.model.*;
import com.gerenciadortarefas.auth.service.*;
import com.gerenciadortarefas.model.TarefaRepository;
import com.gerenciadortarefas.model.TarefaRepositoryH2;
import com.gerenciadortarefas.service.TarefaService;
import com.gerenciadortarefas.service.TarefaServiceImpl;
import com.gerenciadortarefas.util.EnvLoader;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class GerenciadorTarefas {
    public static void main(String[] args) {
        EnvLoader.load();

        // ---------- Auth wiring ----------
        UsuarioRepository usuarioRepo = new UsuarioRepositoryH2();
        SessaoRepository  sessaoRepo  = new SessaoRepositoryH2();
        MfaPendingRepository mfaRepo  = new MfaPendingRepositoryH2();

        PasswordHasher hasher = new PasswordHasher();
        TokenService tokens   = new TokenService();
        CryptoService crypto  = new CryptoService();
        TotpService totp      = new TotpService();
        AuthService authService = new AuthService(usuarioRepo, sessaoRepo, mfaRepo,
                                                  hasher, tokens, crypto, totp);

        // ---------- Tarefas wiring ----------
        TarefaRepository tarefaRepo = new TarefaRepositoryH2();
        TarefaService tarefaService = new TarefaServiceImpl(tarefaRepo);

        // ---------- Servidor ----------
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        AuthHandler authHandler = new AuthHandler(authService);
        SessionFilter sessionFilter = new SessionFilter(authService);
        CsrfFilter csrfFilter = new CsrfFilter();

        // Rotas públicas de auth
        server.createContext("/auth", authHandler);

        // Rotas autenticadas de auth (mesmo handler, mas com SessionFilter)
        HttpContext ctxAuthAccount = server.createContext("/auth/account", authHandler);
        ctxAuthAccount.getFilters().add(sessionFilter);
        // CSRF não é aplicado aqui pois o frontend precisa pegar o token via GET /auth/account/csrf
        // antes de poder enviá-lo. Logout/setup/confirm aceitam sem CSRF na 1ª iteração;
        // adicionar CsrfFilter aqui em hardening posterior.

        // API de tarefas — protegida por sessão + CSRF nos métodos mutadores
        HttpContext ctxTarefas = server.createContext("/api/tarefas", new TarefaHandler(tarefaService));
        ctxTarefas.getFilters().add(sessionFilter);
        ctxTarefas.getFilters().add(csrfFilter);

        // Frontend estático
        server.createContext("/", new StaticHandler());

        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        System.out.println("API em http://localhost:8080");
        System.out.println("  Login:   POST /auth/login");
        System.out.println("  Registro: POST /auth/register");
        System.out.println("  Tarefas: /api/tarefas (sessão obrigatória)");
        System.out.println("Frontend em http://localhost:8080/");
    }
}

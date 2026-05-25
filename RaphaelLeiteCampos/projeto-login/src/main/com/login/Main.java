package com.login;

import com.login.controller.AuthController;
import com.login.repository.UsuarioRepository;
import com.login.server.ServidorHttp;
import com.login.service.AuthService;
import com.login.util.DatabaseConfig;

import java.io.IOException;

/**
 * PONTO DE ENTRADA DA APLICAÇÃO
 * ==============================
 * Aqui fica a "composição" de todos os objetos (Dependency Injection manual).
 * Em produção, frameworks como Spring fazem isso automaticamente.
 *
 * Fluxo de inicialização:
 *  1. Configura banco de dados
 *  2. Cria as dependências na ordem correta (Repository → Service → Controller)
 *  3. Registra as rotas no servidor HTTP
 *  4. Inicia o servidor
 *
 * Isso é chamado de "Composition Root" — um único lugar onde
 * toda a fiação da aplicação acontece.
 */
public class Main {

    public static void main(String[] args) throws IOException {

        System.out.println("=== Sistema de Login - Aula MVC ===\n");

        // 1. Banco de dados
        DatabaseConfig.getDataSource();  // Inicializa e cria tabelas

        // 2. Composição das camadas (de baixo para cima)
        UsuarioRepository repository = new UsuarioRepository();
        AuthService service           = new AuthService(repository);
        AuthController controller     = new AuthController(service);

        // 3. Servidor HTTP na porta 8080
        ServidorHttp servidor = new ServidorHttp(8080);

        // 4. Registro de rotas
        servidor.registrarRota("/api/login",    controller::login);
        servidor.registrarRota("/api/cadastro", controller::cadastro);

        // 5. Graceful shutdown — fecha o banco ao encerrar a JVM
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConfig.fechar();
            System.out.println("\nServidor encerrado.");
        }));

        // 6. Inicia!
        servidor.iniciar();
    }
}

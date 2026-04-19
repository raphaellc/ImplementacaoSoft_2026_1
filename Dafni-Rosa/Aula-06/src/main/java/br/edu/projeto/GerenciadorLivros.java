package br.edu.projeto;

import br.edu.projeto.api.LivroHandler;
import br.edu.projeto.api.StaticHandler;
import br.edu.projeto.model.LivroRepository;
import br.edu.projeto.model.LivroRepositoryH2;
import br.edu.projeto.service.LivroService;
import br.edu.projeto.service.LivroServiceImpl;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class GerenciadorLivros {

    public static void main(String[] args) throws IOException {
        // 1. Cria o repositório concreto (H2 em memória)
        LivroRepository repository = new LivroRepositoryH2();

        // 2. Injeta repositório no serviço
        LivroService service = new LivroServiceImpl(repository);

        // 3. Cria servidor HTTP na porta 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 4. Registra handlers
        server.createContext("/api/livros", new LivroHandler(service));
        server.createContext("/", new StaticHandler());

        // 5. Usa o executor padrão da JVM (thread por requisição)
        server.setExecutor(null);

        // 6. Inicia o servidor
        server.start();

        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║     Gerenciador de Livros iniciado!    ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║  Frontend : http://localhost:8080      ║");
        System.out.println("║  API REST : http://localhost:8080/api/livros ║");
        System.out.println("╚════════════════════════════════════════╝");
    }
}
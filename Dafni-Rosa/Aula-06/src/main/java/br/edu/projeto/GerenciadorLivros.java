package br.edu.projeto;

import br.edu.projeto.api.LivroHandler;
import br.edu.projeto.controller.LivroController;
import br.edu.projeto.model.LivroRepository;
import br.edu.projeto.model.LivroRepositoryMySQL;
import br.edu.projeto.service.LivroService;
import br.edu.projeto.service.LivroServiceImpl;
import br.edu.projeto.view.LivroView;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Ponto de entrada da aplicação.
 *
 * Uso:
 *   java -jar gerenciador-livros.jar          → modo console (padrão)
 *   java -jar gerenciador-livros.jar api      → modo API HTTP (porta 8080)
 */
public class GerenciadorLivros {

    public static void main(String[] args) throws IOException {

        // Wiring: instancia as dependências manualmente (sem framework)
        LivroRepository repository = new LivroRepositoryMySQL();
        LivroService    service    = new LivroServiceImpl(repository);

        boolean modoApi = args.length > 0 && args[0].equalsIgnoreCase("api");

        if (modoApi) {
            iniciarModoApi(service);
        } else {
            iniciarModoConsole(service);
        }
    }

    // -----------------------------------------------------------------------
    // Modo console — igual ao GerenciadorTarefas original
    // -----------------------------------------------------------------------
    private static void iniciarModoConsole(LivroService service) {
        LivroView       view       = new LivroView();
        LivroController controller = new LivroController(view, service);
        controller.iniciarGerenciadorLivros();
    }

    // -----------------------------------------------------------------------
    // Modo API HTTP
    // -----------------------------------------------------------------------
    private static void iniciarModoApi(LivroService service) throws IOException {
        int porta = Integer.parseInt(System.getenv().getOrDefault("API_PORT", "8080"));

        HttpServer server = HttpServer.create(new InetSocketAddress(porta), 0);
        server.createContext("/api/livros", new LivroHandler(service));
        server.setExecutor(null); // usa o executor padrão
        server.start();

        System.out.println("API iniciada em http://localhost:" + porta + "/api/livros");
        System.out.println("Pressione Ctrl+C para encerrar.");
    }
}
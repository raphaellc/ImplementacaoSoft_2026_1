package TarefaAula5;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class LivroHandler implements HttpHandler {
    private final LivroRepository repository;
    private static final ObjectMapper mapper = new ObjectMapper();

    public LivroHandler(LivroRepository repository) {
        this.repository = repository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String metodo = exchange.getRequestMethod(); // Identifica o verbo HTTP [cite: 49]

        try {
            switch (metodo) {
                case "GET" -> listarLivros(exchange);
                case "POST" -> adicionarLivro(exchange);
                case "PUT" -> processarAluguelDevolucao(exchange);
                default -> enviarResposta(exchange, 405, "Método não permitido");
            }
        } catch (Exception e) {
            enviarResposta(exchange, 500, "Erro: " + e.getMessage());
        }
    }

    // 1. GET: Lista todos os livros [cite: 27]
    private void listarLivros(HttpExchange exchange) throws IOException {
        List<Livro> livros = repository.listarLivros();
        // Serialização: Converte a lista de objetos Java para bytes JSON [cite: 62, 65, 89]
        byte[] response = mapper.writeValueAsBytes(livros);

        // Define o cabeçalho como JSON para o navegador entender
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        enviarBytes(exchange, 200, response); // Status 200: Sucesso [cite: 20]
    }

    // 2. POST: Cria um novo livro [cite: 33, 34]
    private void adicionarLivro(HttpExchange exchange) throws IOException {
        // Desserialização: Lê o JSON do corpo da requisição e transforma em mapa/objeto [cite: 68, 69]
        Map<String, String> body = mapper.readValue(exchange.getRequestBody(), Map.class);
        String titulo = body.get("titulo");

        if (titulo != null && !titulo.isBlank()) {
            repository.AdicionarLivro(titulo);
            enviarResposta(exchange, 201, "Livro adicionado!"); // Status 201: Criado [cite: 56, 57]
        } else {
            enviarResposta(exchange, 400, "Titulo invalido");
        }
    }

    // 3. PUT: Atualiza o estado (alugar/devolver) [cite: 38, 39]
    private void processarAluguelDevolucao(HttpExchange exchange) throws IOException {
        Map<String, Object> body = mapper.readValue(exchange.getRequestBody(), Map.class);
        int id = (int) body.get("id");
        String acao = (String) body.get("acao"); // "alugar" ou "devolver"

        boolean sucesso = acao.equals("alugar") ?
                repository.marcarComoAlugado(id) :
                repository.marcarComoDisponivel(id);

        if (sucesso) {
            enviarResposta(exchange, 200, "Operacao realizada com sucesso");
        } else {
            enviarResposta(exchange, 404, "Livro nao encontrado ou operacao invalida");
        }
    }

    // Métodos auxiliares para envio de resposta [cite: 57, 92, 93]
    private void enviarResposta(HttpExchange exchange, int statusCode, String resposta) throws IOException {
        enviarBytes(exchange, statusCode, resposta.getBytes());
    }

    private void enviarBytes(HttpExchange exchange, int statusCode, byte[] response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }
}
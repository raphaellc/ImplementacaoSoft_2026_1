package br.edu.projeto;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import br.edu.projeto.api.LivroHandler;
import br.edu.projeto.model.LivroRepository;
import br.edu.projeto.service.LivroService;
import br.edu.projeto.service.LivroServiceImpl;

public class Main {
    public static void main(String[] args) throws Exception {
        LivroRepository repository = new LivroRepository();
        LivroService service = new LivroServiceImpl(repository); 
        
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        server.createContext("/api/livros", new LivroHandler(service));
        
        server.setExecutor(null); 
        
        System.out.println("Iniciando API...");
        System.out.println("Endereço da API: http://localhost:8080/api/livros");
        
        server.start();
    }
}
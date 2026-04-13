package com.gerenciadortarefas;

import com.gerenciadortarefas.model.TarefaRepository;
import com.gerenciadortarefas.model.TarefaRepositoryH2;
import com.gerenciadortarefas.util.EnvLoader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.gerenciadortarefas.api.StaticHandler;
import com.gerenciadortarefas.api.TarefaHandler;
import com.sun.net.httpserver.HttpServer;
import com.gerenciadortarefas.service.TarefaService;
import com.gerenciadortarefas.service.TarefaServiceImpl;

public class GerenciadorTarefas {
    public static void main(String[] args) {
        EnvLoader.load(); // deve ser a primeira linha — carrega o .env antes de qualquer conexão
       //TarefaView view = new TarefaView();
        TarefaRepository repositoryH2 = new TarefaRepositoryH2();
        TarefaRepository repository = repositoryH2;
        
        TarefaService service = new TarefaServiceImpl(repository);
       
        /* 
        TarefaController controller = new TarefaController(view, service);
        controller.iniciarGerenciadorTarefas();
    
        System.out.println("Gerenciador de Tarefas iniciado com sucesso."); */

        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        server.createContext("/api/tarefas", new TarefaHandler(service));
        server.createContext("/", new StaticHandler());

        // Falha 5 corrigida: limita a 10 threads simultâneas para evitar esgotamento de memória
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        System.out.println("API rodando em http://localhost:8080/api/tarefas");
        System.out.println("Frontend disponível em http://localhost:8080/");
    }

}
package com.gerenciadortarefas;

import com.gerenciadortarefas.model.TarefaRepository;
import com.gerenciadortarefas.model.TarefaRepositoryH2;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.gerenciadortarefas.api.StaticHandler;
import com.gerenciadortarefas.api.TarefaHandler;
import com.sun.net.httpserver.HttpServer;
import com.gerenciadortarefas.service.TarefaService;
import com.gerenciadortarefas.service.TarefaServiceImpl;

public class GerenciadorTarefas {
    public static void main(String[] args) {
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

        server.setExecutor(null);
        server.start();
        System.out.println("API rodando em http://localhost:8080/api/tarefas");
        System.out.println("Frontend disponível em http://localhost:8080/");
    }

}
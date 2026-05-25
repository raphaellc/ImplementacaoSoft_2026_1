package com.gerenciadortarefas.controller;

import java.util.List;
import java.util.Optional;
import com.gerenciadortarefas.service.*;
import com.gerenciadortarefas.model.*;
import com.gerenciadortarefas.view.*;

public class TarefaController {
    // CLI desativado — usuário sintético só para a interface CLI continuar compilando.
    // Em produção o usuarioId vem do SessionFilter.
    private static final int USUARIO_CLI = 1;

    private TarefaView tarefa_view;
    private TarefaService tarefa_service;

    public TarefaController(TarefaView view, TarefaService service){
        this.tarefa_view = view;
        this.tarefa_service = service;
    }

    public void iniciarGerenciadorTarefas(){
        int opcao;
        do {
            opcao = tarefa_view.montarMenu();
            switch(opcao){
                case 1:
                    adicionarTarefa();
                    break;
                case 2:
                    listarTarefas();
                    break;
                case 3:
                    marcarTarefaConcluida();
                    break;
                case 0:
                    tarefa_view.exibirMensagem("Saindo do sistema.");
                    break;
                default:
                    System.out.println("Opção inválida.");
                    break;
            }    
        } while (opcao != 0);
        

    }

    public void adicionarTarefa(){
        String descricao = tarefa_view.adicionarTarefa();
        tarefa_service.adicionarTarefa(USUARIO_CLI, descricao);
        listarTarefas();

    }
    public void listarTarefas(){
        List<Tarefa> tarefas = tarefa_service.listarTarefas(USUARIO_CLI);
        tarefa_view.listarTarefas(tarefas);

    }

    public void marcarTarefaConcluida() {
        int id = tarefa_view.marcarTarefaConcluida();
        Optional<Tarefa> resultado = tarefa_service.marcarTarefaConcluida(USUARIO_CLI, id);
        if (resultado.isPresent()) {
            tarefa_view.exibirMensagem("Tarefa " + id + " marcada como concluída.");
        } else {
            tarefa_view.exibirMensagem("Tarefa com ID " + id + " não encontrada.");
        }
    }
}

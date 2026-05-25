package com.gerenciadortarefas.controller;

import java.util.List;
import java.util.Optional;

import com.gerenciadortarefas.model.*;
import com.gerenciadortarefas.view.*;

public class TarefaController {
    private TarefaView tarefa_view;
    private TarefaRepository tarefa_repository;


    public TarefaController(TarefaView view, TarefaRepository repository){
        this.tarefa_view = view;
        this.tarefa_repository = repository;
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
        tarefa_repository.adicionarTarefa(descricao);
        listarTarefas();
        
    }
    public void listarTarefas(){
        List<Tarefa> tarefas = tarefa_repository.listarTarefas();
        tarefa_view.listarTarefas(tarefas);

    }
    private String marcarTarefaConcluida(int id){
        Optional<Tarefa> tarefaOptional = tarefa_repository.buscarPorId(id);
        if (tarefaOptional.isPresent()) {
            Tarefa tarefa = tarefaOptional.get();
            Tarefa tarefaModificada = tarefa.comConcluida(true);
            if (tarefa_repository.atualizarTarefa(tarefaModificada)) {
                return "Tarefa " + id + " marcada como concluída.";
            } 
            return "Erro ao atualizar a tarefa.";
        }
        return "Tarefa com ID " + id + " não encontrada.";
    }

    public void marcarTarefaConcluida() {
        int id = tarefa_view.marcarTarefaConcluida();
        tarefa_view.exibirMensagem(marcarTarefaConcluida(id));

    }
}

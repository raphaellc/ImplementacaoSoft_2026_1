package com.gerenciadortarefas;

import com.gerenciadortarefas.model.*;
import com.gerenciadortarefas.controller.TarefaController;
import com.gerenciadortarefas.view.TarefaView;

public class GerenciadorTarefas {
    public static void main(String[] args) {
        TarefaView view = new TarefaView();
        TarefaRepository repository = new TarefaRepository();
        
        TarefaController controller = new TarefaController(view, repository);
        controller.iniciarGerenciadorTarefas();
    

        System.out.println("Gerenciador de Tarefas iniciado com sucesso.");
    }

}
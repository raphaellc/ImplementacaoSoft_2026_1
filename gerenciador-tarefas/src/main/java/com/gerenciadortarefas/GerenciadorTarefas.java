package com.gerenciadortarefas;

import com.gerenciadortarefas.model.*;
import com.gerenciadortarefas.controller.TarefaController;
import com.gerenciadortarefas.view.TarefaView;
import com.gerenciadortarefas.service.TarefaService;
import com.gerenciadortarefas.service.TarefaServiceImpl;

public class GerenciadorTarefas {
    public static void main(String[] args) {
        TarefaView view = new TarefaView();
        TarefaRepository repository = new TarefaRepository();
        TarefaService service = new TarefaServiceImpl(repository);
        
        TarefaController controller = new TarefaController(view, service);
        controller.iniciarGerenciadorTarefas();
    

        System.out.println("Gerenciador de Tarefas iniciado com sucesso.");
    }

}
package br.edu.projeto;

import br.edu.projeto.controller.TarefaController;
import br.edu.projeto.model.TarefaService;
import br.edu.projeto.view.TarefaView;

public class Main {

    public static void main(String[] args) {

        // cria as partes
        TarefaService service = new TarefaService();
        TarefaView view = new TarefaView();

        // injeta no controller
        TarefaController controller = new TarefaController(service, view);

        // inicia o sistema
        controller.iniciar();
    }
}
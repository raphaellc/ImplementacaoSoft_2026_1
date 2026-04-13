package br.edu.projeto;

import br.edu.projeto.controller.TarefaController;
import br.edu.projeto.model.TarefaRepository;
import br.edu.projeto.view.TarefaView;

public class Main {

    public static void main(String[] args) {

        TarefaRepository repository = new TarefaRepository();
        TarefaView view = new TarefaView();

        TarefaController controller = new TarefaController(repository, view);

        controller.iniciar();
    }
}
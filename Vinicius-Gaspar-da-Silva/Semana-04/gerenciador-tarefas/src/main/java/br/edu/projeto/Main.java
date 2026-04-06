package br.edu.projeto;

import br.edu.projeto.controller.TarefaController;
import br.edu.projeto.model.TarefaRepository;
import br.edu.projeto.view.TarefaView;

public class Main {
    public static void main(String[] args) {
        TarefaController app = new TarefaController(new TarefaRepository(), new TarefaView());
        app.iniciar();
    }
}
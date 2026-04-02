package br.edu.projeto;

import br.edu.projeto.controller.TarefaController;
import br.edu.projeto.model.TarefaRepositorio;
import br.edu.projeto.view.TarefaView;

public class Main {
    public static void main(String[] args) {
        TarefaRepositorio repositorio = new TarefaRepositorio();
        TarefaView view = new TarefaView();
        TarefaController controller = new TarefaController(repositorio, view);
        controller.iniciar();
        view.fechar();
    }
}

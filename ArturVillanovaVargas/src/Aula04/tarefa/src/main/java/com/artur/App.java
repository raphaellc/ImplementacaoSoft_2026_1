package Aula04.tarefa.src.main.java.com.artur;

import Aula04.tarefa.src.main.java.com.artur.controller.TarefaController;
import Aula04.tarefa.src.main.java.com.artur.view.TarefaView;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        var controller = new TarefaController();
        var view = new TarefaView(controller);

        view.apresentarMenu();
    }
}

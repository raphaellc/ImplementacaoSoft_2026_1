package eduardofettermann.gerenciamentodetarefas;

import eduardofettermann.gerenciamentodetarefas.controller.TarefaController;
import eduardofettermann.gerenciamentodetarefas.model.TarefaRepositorio;
import eduardofettermann.gerenciamentodetarefas.service.TarefaService;
import eduardofettermann.gerenciamentodetarefas.view.TarefaView;

public class Main {

    public static void main(String[] args) {
        TarefaRepositorio repositorio = new TarefaRepositorio();
        TarefaService servico = new TarefaService(repositorio);
        TarefaView view = new TarefaView();
        TarefaController controller = new TarefaController(servico, view);
        controller.executar();
    }
}

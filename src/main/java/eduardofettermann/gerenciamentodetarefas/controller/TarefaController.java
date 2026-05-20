package eduardofettermann.gerenciamentodetarefas.controller;

import eduardofettermann.gerenciamentodetarefas.service.TarefaService;
import eduardofettermann.gerenciamentodetarefas.view.TarefaView;
import java.util.List;

public class TarefaController {

    private final TarefaService servico;
    private final TarefaView view;

    public TarefaController(TarefaService servico, TarefaView view) {
        this.servico = servico;
        this.view = view;
    }

    public void executar() {
        boolean continuar = true;
        while (continuar) {
            view.exibeMenu();
            String opcaoMenu = view.exibeERecebeResposta("Opcao: ");

            switch (opcaoMenu) {
                case "1" -> adicionaTarefa();
                case "2" -> exibirTarefas();
                case "3" -> concluirTarefa();
                case "4" -> {
                    view.exibeMensagem("Encerrando o programa...");
                    continuar = false;
                }
                default -> view.exibeMensagem("Opcao invalida.");
            }
        }
    }

    private void adicionaTarefa() {
        String descricao = view.exibeERecebeResposta("Escreva a descricao da tarefa: ");
        if (descricao.isBlank()) {
            view.exibeMensagem("Descricao nao pode ser vazia.");
            return;
        }
        servico.adicionar(descricao);
        view.exibeMensagem("Tarefa adicionada.");
    }

    private void concluirTarefa() {
        String idDigitado = view.exibeERecebeResposta("Id da tarefa: ");
        int idTarefa = Integer.parseInt(idDigitado.trim());
        if (idTarefa < 1) {
            view.exibeMensagem("Id invalido.");
            return;
        }
        if (servico.marcarConcluida(idTarefa)) {
            view.exibeMensagem("Tarefa marcada como concluida.");
        } else {
            view.exibeMensagem("Tarefa nao encontrada.");
        }
    }

    private void exibirTarefas() {
        view.exibeTarefas(servico.listarTodas());
    }
}

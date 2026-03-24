package AugustoFeltrin.Aula04.controller;

import AugustoFeltrin.Aula04.model.TarefaRepository;
import AugustoFeltrin.Aula04.view.TarefaView;

public class TarefaController {
    private final TarefaRepository model;
    private final TarefaView view;

    public TarefaController(TarefaRepository model, TarefaView view) {
        this.model = model;
        this.view = view;
    }

    public void iniciar() {
        boolean rodando = true;
        while (rodando) {
            String opcao = view.exibirMenu();

            switch (opcao) {
                case "1" -> model.adicionar(view.pedirDescricao());
                case "2" -> view.mostrarTarefas(model.listarTodas());
                case "3" -> model.marcarComoConcluida(view.pedirId());
                case "4" -> rodando = false;
                default -> System.out.println("Opção inválida!");
            }
        }
    }
}
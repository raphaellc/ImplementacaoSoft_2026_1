package br.edu.projeto.controller;

import br.edu.projeto.model.TarefaRepository;
import br.edu.projeto.view.TarefaView;

public class TarefaController {
    private final TarefaRepository repository;
    private final TarefaView view;

    public TarefaController(TarefaRepository repository, TarefaView view) {
        this.repository = repository;
        this.view = view;
    }

    public void iniciar() {
        boolean continuar = true;
        while (continuar) {
            String opcao = view.exibirMenu();

            switch (opcao) {
                case "1": 
                    String descricao = view.pedirDescricao();
                    repository.adicionarTarefa(descricao);
                    view.mensagem("Adicionada!");
                    break;
                case "2": 
                    view.mostrarTarefas(repository.listarTarefas());
                    break;
                case "3": 
                    int id = view.pedirId();
                    if (repository.marcarComoConcluida(id)) {
                          view.mensagem("Concluída!");
                    } else view.mensagem("ID inválido.");
                    break;
                
                case "0":
                    continuar = false;
                    break;
                default: 
                    System.out.println("Opção inválida");
                    break;
            }
        }
    }
}
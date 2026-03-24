package Aula04.tarefa.src.main.java.com.artur.view;

import Aula04.tarefa.src.main.java.com.artur.controller.TarefaController;
import Aula04.tarefa.src.main.java.com.artur.model.Tarefa;
import java.util.Scanner;

public class TarefaView {

    private Scanner scanner = new Scanner(System.in);
    private final TarefaController controller;

    public TarefaView(TarefaController controller) {
       this.controller = controller;
    }

    public void apresentarMenu() {
        while(true) {
            System.out.println("------------------------");
            System.out.println("1 - Adicionar tarefas");
            System.out.println("2 - Listar tarefas");
            System.out.println("3 - Concluir tarefa");
            System.out.println("------------------------");
            System.out.println("Escolha uma opção: ");

            escolherOpcao(Integer.parseInt(this.scanner.nextLine()));
        }
    }

    private void apresentarAdicionarTarefa() {
        System.out.println("Digite a descrição da tarefa: ");
        String descricao = this.scanner.nextLine();

       adicionarTarefa(descricao);
    }

    private void apresentarListarTarefas() {
        System.out.println("Tarefas: ");
        System.out.println("-----------------");
        System.out.println(listarTarefas());;
    }

    private void apresentarConcluirTarefa() {
        System.out.println("Digite o id da tarefa que deseja concluir: ");
        int id = Integer.parseInt(this.scanner.nextLine());

        concluirTarefa(id);
    }

    private void escolherOpcao(int opcao) {
        switch (opcao) {
            case 1: apresentarAdicionarTarefa();
                break;
            case 2: apresentarListarTarefas();
                break;
            case 3: apresentarConcluirTarefa();
                break;
            default:
                System.out.println("Opção inválida!");
                break;
        }
    }

    private void adicionarTarefa(String descricao) {
        controller.adicionarTarefa(descricao);
    }

    private String listarTarefas() {
        return controller.listarTarefas();
    }

    private void concluirTarefa(int id) {
        controller.concluirTarefa(id);
    }
}

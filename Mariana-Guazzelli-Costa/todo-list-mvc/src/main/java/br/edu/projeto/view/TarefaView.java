package br.edu.projeto.view;

import br.edu.projeto.model.Tarefa;

import java.util.List;
import java.util.Scanner;

public class TarefaView {

    private final Scanner scanner = new Scanner(System.in);

    public String mostrarMenu() {
        System.out.println("\n=== TO-DO LIST ===");
        System.out.println("1 - Adicionar tarefa");
        System.out.println("2 - Listar tarefas");
        System.out.println("3 - Concluir tarefa");
        System.out.println("0 - Sair");
        System.out.print("\nEscolha: ");
        return scanner.nextLine();
    }

    public String lerDescricao() {
        System.out.print("Descrição da tarefa: ");
        return scanner.nextLine();
    }

    public int lerId() {
        System.out.print("ID da tarefa: ");
        return Integer.parseInt(scanner.nextLine());
    }

    public void mostrarTarefas(List<Tarefa> tarefas) {
        if (tarefas.isEmpty()) {
            System.out.println("Nenhuma tarefa cadastrada.");
            return;
        }

        for (Tarefa t : tarefas) {
            System.out.println(
                    t.id() + " - " + t.descricao() +
                            (t.concluida() ? " [✔]" : " [ ]")
            );
        }
    }

    public void mostrarMensagem(String mensagem) {
        System.out.println(mensagem);
    }
}
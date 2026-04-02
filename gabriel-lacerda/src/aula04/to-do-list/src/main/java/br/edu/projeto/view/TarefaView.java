package br.edu.projeto.view;

import br.edu.projeto.model.Tarefa;

import java.util.List;
import java.util.Scanner;

public class TarefaView {

    private final Scanner scanner = new Scanner(System.in);

    public int exibirMenu() {
        System.out.println("\n--- TO-DO LIST ---");
        System.out.println("1. Adicionar tarefa");
        System.out.println("2. Listar tarefas");
        System.out.println("3. Concluir tarefa");
        System.out.println("0. Sair");
        System.out.print("Opcao: ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String lerDescricao() {
        System.out.print("Descricao: ");
        return scanner.nextLine();
    }

    public int lerId() {
        System.out.print("ID da tarefa: ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void exibirTarefas(List<Tarefa> tarefas) {
        if (tarefas.isEmpty()) {
            System.out.println("Nenhuma tarefa cadastrada.");
            return;
        }
        System.out.println("\n--- Tarefas ---");
        tarefas.forEach(t -> System.out.println(t.exibir()));
    }

    public void exibirMensagem(String mensagem) {
        System.out.println(mensagem);
    }

    public void fechar() {
        scanner.close();
    }
}

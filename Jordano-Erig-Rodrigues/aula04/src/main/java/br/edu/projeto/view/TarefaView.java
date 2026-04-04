package br.edu.projeto.view;

import br.edu.projeto.model.Tarefa;
import java.util.List;
import java.util.Scanner;

public class TarefaView {

    private final Scanner scanner = new Scanner(System.in);

    public void exibirMenu() {
        System.out.println("\n=== TO-DO LIST ===");
        System.out.println("1. Adicionar tarefa");
        System.out.println("2. Listar tarefas");
        System.out.println("3. Concluir tarefa");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    public String lerOpcao() {
        return scanner.nextLine().trim();
    }

    public String lerDescricao() {
        System.out.print("Digite a descrição da tarefa: ");
        return scanner.nextLine();
    }

    public int lerId() {
        System.out.print("Digite o ID da tarefa: ");
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
        tarefas.forEach(System.out::println);
    }

    public void exibirMensagem(String mensagem) {
        System.out.println(mensagem);
    }
}
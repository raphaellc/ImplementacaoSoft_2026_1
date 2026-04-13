package br.edu.projeto.view;

import br.edu.projeto.model.Tarefa;

import java.util.List;
import java.util.Scanner;

public class TarefaView {

    private Scanner scanner = new Scanner(System.in);

    // mostrar menu
    public String mostrarMenu() {
        System.out.println("\n=== MENU ===");
        System.out.println("1 - Adicionar tarefa");
        System.out.println("2 - Listar tarefas");
        System.out.println("3 - Concluir tarefa");
        System.out.println("0 - Sair");
        System.out.print("Escolha: ");

        return scanner.nextLine();
    }

    // pedir descrição
    public String pedirDescricao() {
        System.out.print("Digite a descrição da tarefa: ");
        return scanner.nextLine();
    }

    // pedir id
    public int pedirId() {
        System.out.print("Digite o ID da tarefa: ");
        return Integer.parseInt(scanner.nextLine());
    }

    // mostrar tarefas
    public void mostrarTarefas(List<Tarefa> tarefas) {
        System.out.println("\n=== LISTA DE TAREFAS ===");

        if (tarefas.isEmpty()) {
            System.out.println("Nenhuma tarefa cadastrada.");
            return;
        }

        for (Tarefa t : tarefas) {
            String status = t.concluida() ? "✔" : "❌";
            System.out.println(t.id() + " - " + t.descricao() + " [" + status + "]");
        }
    }

    public void mostrarMensagem(String mensagem) {
        System.out.println(mensagem);
    }
}
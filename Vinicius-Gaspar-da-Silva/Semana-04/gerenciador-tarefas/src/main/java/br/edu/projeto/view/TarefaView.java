package br.edu.projeto.view;

import java.util.List;
import java.util.Scanner;

import br.edu.projeto.model.Tarefa;

public class TarefaView {
    private final Scanner scanner = new Scanner(System.in);

    public String exibirMenu() {
        System.out.println("\nMENU:");
        System.out.println("1: Adicionar");
        System.out.println("2: Listar");
        System.out.println("3: Concluir");
        System.out.println("0: Sair");
        System.out.print("Opção: ");
        return scanner.nextLine();
    }

    public void mostrarTarefas(List<Tarefa> lista) {
        if (lista.isEmpty()) {
            System.out.println("Nenhuma tarefa.");
        } 

        for(Tarefa tarefa : lista){
            System.out.println("-" + tarefa.toString());
        }
    }

    public String pedirDescricao() {
        System.out.print("Descrição: ");
        return scanner.nextLine();
    }

    public int pedirId() {
        System.out.print("ID da tarefa: ");
        int id = scanner.nextInt();
        return id;
    }

    public void mensagem(String texto) {
        System.out.println(texto);
    }
}
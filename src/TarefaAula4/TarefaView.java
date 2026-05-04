package TarefaAula4;

import java.util.Scanner;

public class TarefaView {

    private Scanner scanner = new Scanner(System.in);

    public int apresentarMenu() {
        System.out.println("------------------------");
        System.out.println("1 - Adicionar tarefas");
        System.out.println("2 - Listar tarefas");
        System.out.println("3 - Concluir tarefa");
        System.out.println("------------------------");
        System.out.println("Escolha uma opção: ");

        return Integer.parseInt(scanner.nextLine());
    }

    public String lerDescricaoTarefa() {
        System.out.println("Digite a descrição da tarefa:");
        return scanner.nextLine();
    }

    public int lerIdTarefa() {
        System.out.println("Digite o id da tarefa:");
        return Integer.parseInt(scanner.nextLine());
    }

    public void mostrarTarefas(String tarefas) {
        System.out.println("Tarefas:");
        System.out.println("-----------------");
        System.out.println(tarefas);
    }

    public void mostrarMensagem(String mensagem) {
        System.out.println(mensagem);
    }
}

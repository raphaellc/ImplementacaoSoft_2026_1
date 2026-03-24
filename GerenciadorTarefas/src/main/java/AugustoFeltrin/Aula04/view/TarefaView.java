package AugustoFeltrin.Aula04.view;

import AugustoFeltrin.Aula04.model.Tarefa;
import java.util.List;
import java.util.Scanner;

public class TarefaView {
    private Scanner leitor = new Scanner(System.in);

    public String exibirMenu() {
        System.out.println("\n--- Gerenciador de Tarefas ---");
        System.out.println("1. Adicionar Tarefa");
        System.out.println("2. Listar Tarefas");
        System.out.println("3. Concluir Tarefa");
        System.out.println("4. Sair");
        System.out.print("Escolha: ");
        return leitor.nextLine();
    }

    public void mostrarTarefas(List<Tarefa> tarefas) {
        if (tarefas.isEmpty()) {
            System.out.println("Nenhuma tarefa cadastrada.");
        } else {
            for (Tarefa t : tarefas) {
                String status;
                if (t.concluida()) {
                    status = "OK";
                } else {
                    status = "Pendente";
                }
                System.out.println("[" + t.id() + "] " + t.descricao() + " (" + status + ")");
            }
        }
    }

    public String pedirDescricao() {
        System.out.print("Descrição da tarefa: ");
        return leitor.nextLine();
    }

    public int pedirId() {
        System.out.print("ID da tarefa: ");
        try {
            return Integer.parseInt(leitor.nextLine());
        } catch (Exception e) {
            return -1;
        }
    }
}
package br.edu.projeto.view;
 
import br.edu.projeto.model.Tarefa;
 
import java.util.List;
import java.util.Scanner;

public class TarefaView {
 
    private final Scanner scanner;
 
    public TarefaView() {
        this.scanner = new Scanner(System.in);
    }
 
   
    public String exibirMenuEObterOpcao() {
        System.out.println("   GERENCIADOR DE  TAREFAS      ");
        System.out.println("══════════════════════════════");
        System.out.println("  1 - Adicionar tarefa        ");
        System.out.println("  2 - Listar tarefas          ");
        System.out.println("  3 - Concluir tarefa         ");
        System.out.println("  0 - Sair                    ");
        System.out.println("══════════════════════════════");
        System.out.print("Escolha uma opção: ");
        return scanner.nextLine().trim();
    }
 
  
    public String solicitarDescricaoTarefa() {
        System.out.print("Digite a descrição da tarefa: ");
        return scanner.nextLine().trim();
    }
 

    public String solicitarIdTarefa() {
        System.out.print("Digite o ID da tarefa: ");
        return scanner.nextLine().trim();
    }
 
  
    public void exibirListaDeTarefas(List<Tarefa> tarefas) {
        System.out.println("\n--- Lista de Tarefas ---");
        if (tarefas.isEmpty()) {
            System.out.println("Nenhuma tarefa cadastrada.");
        } else {
            tarefas.forEach(t -> System.out.println(t.exibir()));
        }
        System.out.println("------------------------");
    }
 
    
    public void exibirTarefaAdicionada(Tarefa tarefa) {
        System.out.println("Tarefa adicionada: " + tarefa.exibir());
    }
 
 
    public void exibirTarefaConcluida(Tarefa tarefa) {
        System.out.println("Tarefa marcada como concluída: " + tarefa.exibir());
    }
 

    public void exibirErro(String mensagem) {
        System.out.println("Erro: " + mensagem);
    }

    public void exibirMensagem(String mensagem) {
        System.out.println(mensagem);
    }
 
    public void exibirDespedida() {
        System.out.println("\nAté logo! Obrigado por usar o Gerenciador de Tarefas.");
    }
 
    public void fechar() {
        scanner.close();
    }
}
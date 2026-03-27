package eduardofettermann.gerenciamentodetarefas.view;

import eduardofettermann.gerenciamentodetarefas.model.Tarefa;
import java.util.List;
import java.util.Scanner;

public class TarefaView {

    private final Scanner scanner = new Scanner(System.in);

    public void exibeMenu() {
        exibeMensagem("");
        exibeMensagem("--- Tarefas ---");
        exibeMensagem("1 - Adicionar tarefa");
        exibeMensagem("2 - Listar tarefas");
        exibeMensagem("3 - Marcar tarefa como concluida");
        exibeMensagem("4 - Sair");
    }

    public String recebeEntrada() {
        return scanner.nextLine();
    }

    public String exibeERecebeResposta(String mensagem) {
        exibeMensagem(mensagem);
        return recebeEntrada();
    }

    public void exibeMensagem(String mensagem) {
        System.out.println(mensagem);
    }

    public void exibeTarefas(List<Tarefa> tarefas) {
        if (tarefas.isEmpty()) {
            exibeMensagem("(Nenhuma tarefa cadastrada.)");
            return;
        }
        
        for (Tarefa t : tarefas) {
            exibeMensagem(t.toString());
        }
    }
}

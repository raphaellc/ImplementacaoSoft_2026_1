package eduardofettermann.gerenciamentodetarefas.model;

import java.util.ArrayList;
import java.util.List;

public class TarefaRepositorio {

    private final List<Tarefa> tarefas = new ArrayList<>();
    private int proximoId = 1;

    public void adicionar(String descricao) {
        tarefas.add(new Tarefa(proximoId++, descricao, false));
    }

    public boolean marcarConcluida(int id) {
        for (int i = 0; i < tarefas.size(); i++) {
            Tarefa t = tarefas.get(i);
            if (t.id() == id) {
                tarefas.set(i, new Tarefa(t.id(), t.descricao(), true));
                return true;
            }
        }
        return false;
    }

    public List<Tarefa> listarTodas() {
        return tarefas;
    }
}

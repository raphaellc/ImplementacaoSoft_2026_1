package AugustoFeltrin.Aula04.model;

import java.util.ArrayList;
import java.util.List;

public class TarefaRepository {
    private final List<Tarefa> tarefas = new ArrayList<>();
    private int proximoId = 1;

    public void adicionar(String descricao) {
        tarefas.add(new Tarefa(proximoId++, descricao, false));
    }

    public List<Tarefa> listarTodas() {
        return new ArrayList<>(tarefas);
    }

    public void marcarComoConcluida(int id) {
        for (int i = 0; i < tarefas.size(); i++) {
            Tarefa t = tarefas.get(i);
            if (t.id() == id) {
                tarefas.set(i, new Tarefa(t.id(), t.descricao(), true));
                return;
            }
        }
    }
}
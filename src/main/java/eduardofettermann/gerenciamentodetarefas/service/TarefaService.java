package eduardofettermann.gerenciamentodetarefas.service;

import eduardofettermann.gerenciamentodetarefas.model.Tarefa;
import eduardofettermann.gerenciamentodetarefas.model.TarefaRepositorio;
import java.util.List;

public class TarefaService {

    private final TarefaRepositorio repositorio;

    public TarefaService(TarefaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void adicionar(String descricao) {
        repositorio.adicionar(descricao);
    }

    public List<Tarefa> listarTodas() {
        return repositorio.listarTodas();
    }

    public boolean marcarConcluida(int id) {
        return repositorio.marcarConcluida(id);
    }
}

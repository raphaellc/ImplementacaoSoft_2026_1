package eduardofettermann.gerenciamentodetarefas.model;

public record Tarefa(int id, String descricao, boolean concluida) {

    @Override
    public String toString() {
        String status = concluida ? "[X]" : "[ ]";
        return "%s %d - %s".formatted(status, id, descricao);
    }
}

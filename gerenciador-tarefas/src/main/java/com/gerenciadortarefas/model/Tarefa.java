package com.gerenciadortarefas.model;

public record Tarefa(int id, int usuarioId, String descricao, boolean concluida) {
    /**
     * Como records são imutáveis, este método cria uma nova instância
     * da tarefa com o status de conclusão atualizado, mantendo demais campos.
     */
    public Tarefa comConcluida(boolean concluida) {
        return new Tarefa(this.id, this.usuarioId, this.descricao, concluida);
    }
}

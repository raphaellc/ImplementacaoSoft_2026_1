package com.gerenciadortarefas.model;

public record Tarefa (int id, String descricao, boolean concluida){
    /**
     * Como records são imutáveis, este método cria uma nova instância 
     * da tarefa com o status de conclusão atualizado, mantendo o ID e a descrição.
     */
    public Tarefa comConcluida(boolean concluida) {
        return new Tarefa(this.id, this.descricao, concluida);
    }
}

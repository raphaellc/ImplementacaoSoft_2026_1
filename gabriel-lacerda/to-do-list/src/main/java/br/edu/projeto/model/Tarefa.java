package br.edu.projeto.model;

public record Tarefa(int id, String descricao, boolean concluida) {
    public Tarefa comConcluida() {
        return new Tarefa(id, descricao, true);
    }
}
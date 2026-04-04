package br.edu.projeto.model;

public record Tarefa(int id, String descricao, boolean concluida) {

    public Tarefa concluir() {
        return new Tarefa(id, descricao, true);
    }

    @Override
    public String toString() {
        String status = concluida ? "[X]" : "[ ]";
        return id + ". " + status + " " + descricao;
    }
}
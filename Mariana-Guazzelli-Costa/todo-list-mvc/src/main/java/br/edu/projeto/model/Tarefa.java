package br.edu.projeto.model;

public record Tarefa(int id, String descricao, boolean concluida) {

    public Tarefa marcarComoConcluida() {
        return new Tarefa(this.id, this.descricao, true);
    }
}
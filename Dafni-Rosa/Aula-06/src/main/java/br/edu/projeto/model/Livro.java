package br.edu.projeto.model;

public record Livro(int id, String titulo, String autor, boolean lido) {

    public Livro comLido(boolean lido) {
        return new Livro(this.id, this.titulo, this.autor, lido);
    }
}
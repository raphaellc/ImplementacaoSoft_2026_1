package br.edu.projeto.model;

public record Livro(int id, String titulo, String autor, boolean disponivel) {

    public Livro comDisponivel(boolean disponivel) {
        return new Livro(this.id, this.titulo, this.autor,  disponivel);
    }

    public Livro comTitulo(String titulo) {
        return new Livro(this.id, titulo, this.autor,  this.disponivel);
    }

    public Livro comAutor(String autor) {
        return new Livro(this.id, this.titulo, autor,  this.disponivel);
    }

    public Livro comIsbn(String isbn) {
        return new Livro(this.id, this.titulo, this.autor, this.disponivel);
    }

    @Override
    public String toString() {
        String status = disponivel ? "Disponível" : "Indisponível";
        return String.format("[ID: %d] %s | Autor: %s | ISBN: %s | Status: %s",
                id, titulo, autor, status);
    }
}
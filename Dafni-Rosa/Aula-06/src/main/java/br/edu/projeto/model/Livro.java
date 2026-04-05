package br.edu.projeto.model;

public record Livro(int id, String titulo, String autor, String isbn, boolean disponivel) {

    /**
     * Como records são imutáveis, este método cria uma nova instância
     * do livro com o campo desejado atualizado, mantendo os demais.
     */
    public Livro comDisponivel(boolean disponivel) {
        return new Livro(this.id, this.titulo, this.autor, this.isbn, disponivel);
    }

    public Livro comTitulo(String titulo) {
        return new Livro(this.id, titulo, this.autor, this.isbn, this.disponivel);
    }

    public Livro comAutor(String autor) {
        return new Livro(this.id, this.titulo, autor, this.isbn, this.disponivel);
    }

    public Livro comIsbn(String isbn) {
        return new Livro(this.id, this.titulo, this.autor, isbn, this.disponivel);
    }

    @Override
    public String toString() {
        String status = disponivel ? "Disponível" : "Indisponível";
        return String.format("[ID: %d] %s | Autor: %s | ISBN: %s | Status: %s",
                id, titulo, autor, isbn, status);
    }
}
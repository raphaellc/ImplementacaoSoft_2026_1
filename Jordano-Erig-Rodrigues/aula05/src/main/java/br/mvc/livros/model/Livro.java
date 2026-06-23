package br.mvc.livros.model;

public record Livro(int id, String titulo, String status) {

	public Livro lendo() {
		return new Livro(id, titulo, "[-]");
	}


	public Livro concluir() {
		return new Livro(id, titulo, "[X]");
	}

	@Override
	public String toString() {
		return id + ". " + status + " " + titulo;
	}
}
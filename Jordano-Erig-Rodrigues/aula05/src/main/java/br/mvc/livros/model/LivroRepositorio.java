package br.mvc.livros.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LivroRepositorio {

	private final ArrayList<Livro> livro = new ArrayList<>();
	private int proximoId = 1;

	public void adicionar(String descricao) {
		livro.add(new Livro(proximoId++, descricao, "[ ]"));
	}

	public List<Livro> listarTodas() {
		return List.copyOf(livro);
	}

	public Optional<Livro> buscarPorId(int id) {
		return livro.stream().filter(t -> t.id() == id).findFirst();
	}

	public boolean concluir(int id) {
		Optional<Livro> encontrada = buscarPorId(id);
		if (encontrada.isPresent()) {
			livro.replaceAll(t -> t.id() == id ? t.concluir() : t);
			return true;
		}
		return false;
	}
	public boolean lendo(int id) {
		Optional<Livro> encontrada = buscarPorId(id);
		if (encontrada.isPresent()) {
			livro.replaceAll(t -> t.id() == id ? t.lendo() : t);
			return true;
		}
		return false;
	}

}

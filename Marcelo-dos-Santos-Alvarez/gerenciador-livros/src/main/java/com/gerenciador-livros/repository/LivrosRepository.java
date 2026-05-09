package com.gerenciadorlivros.repository;

import com.gerenciadorlivros.model.Livro;
import java.util.List;
import java.util.Optional;

public interface LivrosRepository {
	void adicionarLivro(String nome);

	List<Livro> listarLivros();

	Optional<Livro> buscarPorId(int id);

	boolean removerLivro(int id);
}

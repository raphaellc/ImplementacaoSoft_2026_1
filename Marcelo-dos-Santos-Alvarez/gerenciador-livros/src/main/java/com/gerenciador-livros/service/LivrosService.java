package com.gerenciadorlivros.service;

import com.gerenciadorlivros.model.Livro;
import java.util.List;

public interface LivrosService {
	void adicionarLivro(String nome);

	List<Livro> listarLivros();

	boolean removerLivro(int id);
}

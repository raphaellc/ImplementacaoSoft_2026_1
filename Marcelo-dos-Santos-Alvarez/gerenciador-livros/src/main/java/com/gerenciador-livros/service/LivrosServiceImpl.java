package com.gerenciadorlivros.service;

import com.gerenciadorlivros.model.Livro;
import com.gerenciadorlivros.repository.LivrosRepository;
import java.util.List;
import java.util.Optional;

public class LivrosServiceImpl implements LivrosService {
	private LivrosRepository repository;

	public LivrosServiceImpl(LivrosRepository repository) {
		this.repository = repository;
	}

	@Override
	public void adicionarLivro(String nome) {
		if (nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("O nome não pode ser vazio");
		}

		if (nome.length() > 255) {
			throw new IllegalArgumentException("O nome deve ter no máximo 255 caracteres");
		}

		repository.adicionarLivro(nome.trim());
	}

	@Override
	public List<Livro> listarLivros() {
		return repository.listarLivros();
	}
}

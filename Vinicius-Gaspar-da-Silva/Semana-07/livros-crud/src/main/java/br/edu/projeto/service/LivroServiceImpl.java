package br.edu.projeto.service;

import java.util.List;

import br.edu.projeto.model.Livro;
import br.edu.projeto.model.LivroRepository;

public class LivroServiceImpl implements LivroService {
    private final LivroRepository repository;

    public LivroServiceImpl(LivroRepository repository) {
        this.repository = repository;
    }

    @Override
    public void adicionarLivro(String titulo, String autor) {
        if (titulo != null && !titulo.isBlank() && autor != null && !autor.isBlank()) {
            repository.adicionarLivro(titulo, autor);
        }
    }

    @Override
    public List<Livro> listarLivros() {
        return repository.listarLivros();
    }
}

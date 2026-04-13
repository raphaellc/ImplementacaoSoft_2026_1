package br.edu.projeto.service;

import br.edu.projeto.repository.LivroRepository;
import br.edu.projeto.model.Livro;

import java.util.List;

public class LivroService {

    private LivroRepository repository;

    public LivroService(LivroRepository repository) {
        this.repository = repository;
    }

    public void adicionarLivro(String titulo) {
        repository.adicionarLivro(titulo);
    }

    public List<Livro> listarLivros() {
        return repository.listarLivros();
    }
}
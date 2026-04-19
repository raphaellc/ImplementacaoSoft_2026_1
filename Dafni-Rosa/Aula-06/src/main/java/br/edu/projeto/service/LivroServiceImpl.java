package br.edu.projeto.service;

import br.edu.projeto.model.Livro;
import br.edu.projeto.model.LivroRepository;

import java.util.List;
import java.util.Optional;

public class LivroServiceImpl implements LivroService {

    private final LivroRepository repository;

    public LivroServiceImpl(LivroRepository repository) {
        this.repository = repository;
    }

    @Override
    public Livro adicionarLivro(String titulo, String autor) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O título não pode ser vazio");
        }
        if (titulo.length() > 255) {
            throw new IllegalArgumentException("O título deve ter no máximo 255 caracteres");
        }
        if (autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("O autor não pode ser vazio");
        }
        if (autor.length() > 255) {
            throw new IllegalArgumentException("O autor deve ter no máximo 255 caracteres");
        }
        return repository.adicionarLivro(titulo.trim(), autor.trim());
    }

    @Override
    public List<Livro> listarLivros() {
        return repository.listarLivros();
    }

    @Override
    public Optional<Livro> marcarLivroComoLido(int id) {
        Optional<Livro> livroOptional = repository.buscarPorId(id);
        if (livroOptional.isPresent()) {
            Livro livroModificado = livroOptional.get().comLido(true);
            if (repository.atualizarLivro(livroModificado)) {
                return Optional.of(livroModificado);
            }
        }
        return Optional.empty();
    }
}
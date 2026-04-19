package br.edu.projeto.service;

import br.edu.projeto.model.Livro;

import java.util.List;
import java.util.Optional;

public interface LivroService {
    Livro adicionarLivro(String titulo, String autor);
    List<Livro> listarLivros();
    Optional<Livro> marcarLivroComoLido(int id);
}
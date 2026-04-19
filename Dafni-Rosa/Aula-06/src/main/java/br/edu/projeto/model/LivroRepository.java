package br.edu.projeto.model;

import java.util.List;
import java.util.Optional;

public interface LivroRepository {
    Livro adicionarLivro(String titulo, String autor);
    boolean atualizarLivro(Livro livroAtualizado);
    List<Livro> listarLivros();
    Optional<Livro> buscarPorId(int id);
}
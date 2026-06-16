package br.edu.projeto.service;

import java.util.List;

import br.edu.projeto.model.Livro;

public interface LivroService {
    void adicionarLivro(String titulo, String autor);
    List<Livro> listarLivros();
}

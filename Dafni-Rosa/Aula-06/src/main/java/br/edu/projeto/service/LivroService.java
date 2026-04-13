package br.edu.projeto.service;

import br.edu.projeto.model.Livro;

import java.util.List;

public interface LivroService {
    int adicionarLivro(String titulo, String autor, String isbn);
    List<Livro> listarLivros();
    String atualizarLivro(int id, String titulo, String autor, String isbn, boolean disponivel);
    String removerLivro(int id);
    String alterarDisponibilidade(int id, boolean disponivel);
}
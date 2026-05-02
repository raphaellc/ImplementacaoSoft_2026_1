package br.edu.projeto.repository;

import br.edu.projeto.model.Livro;

import java.util.ArrayList;
import java.util.List;

public class LivroRepository {

    private List<Livro> livros = new ArrayList<>();
    private int contadorId = 1;

    public void adicionarLivro(String titulo) {
        Livro livro = new Livro(contadorId++, titulo);
        livros.add(livro);
    }

    public List<Livro> listarLivros() {
        return livros;
    }
}
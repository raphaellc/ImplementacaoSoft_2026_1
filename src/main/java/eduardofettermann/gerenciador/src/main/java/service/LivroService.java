package com.example.gerenciador.service;

import com.example.gerenciador.model.Livro;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LivroService {

    private ArrayList<Livro> livros = new ArrayList<>();
    private int proximoId = 1;

    public void adicionarLivro(String titulo, String autor, int ano) {
        Livro livro = new Livro(proximoId++, titulo, autor, ano, false);
        livros.add(livro);
    }

    public List<Livro> listarLivros() {
        return livros;
    }

    public boolean removerLivro(int id) {
        for (int i = 0; i < livros.size(); i++) {
            if (livros.get(i).getId() == id) {
                livros.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean marcarComoLido(int id) {
        for (Livro livro : livros) {
            if (livro.getId() == id) {
                livro.setLido(true);
                return true;
            }
        }
        return false;
    }
}
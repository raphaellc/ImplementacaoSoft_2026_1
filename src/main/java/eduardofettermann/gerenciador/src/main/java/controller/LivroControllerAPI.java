package com.example.gerenciador.controller;

import com.example.gerenciador.model.Livro;
import com.example.gerenciador.service.LivroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/livros")
public class LivroControllerAPI {

    @Autowired
    private LivroService service;

    @PostMapping
    public String adicionarLivro(@RequestBody Livro livro) {
        service.adicionarLivro(livro.getTitulo(), livro.getAutor(), livro.getAno());
        return "Livro adicionado com sucesso!";
    }

    @GetMapping
    public List<Livro> listarLivros() {
        return service.listarLivros();
    }

    @DeleteMapping("/{id}")
    public String removerLivro(@PathVariable int id) {
        if (service.removerLivro(id)) {
            return "Livro removido com sucesso!";
        }
        return "Livro não encontrado.";
    }

    @PutMapping("/{id}/lido")
    public String marcarComoLido(@PathVariable int id) {
        if (service.marcarComoLido(id)) {
            return "Livro marcado como lido!";
        }
        return "Livro não encontrado.";
    }
}
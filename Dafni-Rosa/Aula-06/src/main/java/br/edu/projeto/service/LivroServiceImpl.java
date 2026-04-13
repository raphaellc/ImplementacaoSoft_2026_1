package br.edu.projeto.service;

import br.edu.projeto.model.Livro;
import br.edu.projeto.model.LivroRepository;

import java.util.List;
import java.util.Optional;

/**
 * Implementação da camada de serviço.
 */
public class LivroServiceImpl implements LivroService {

    private final LivroRepository repository;

    public LivroServiceImpl(LivroRepository repository) {
        this.repository = repository;
    }

    @Override
    public int adicionarLivro(String titulo, String autor, String isbn) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O título não pode ser vazio.");
        }
        if (titulo.length() > 255) {
            throw new IllegalArgumentException("O título deve ter no máximo 255 caracteres.");
        }
        if (autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("O autor não pode ser vazio.");
        }
        if (autor.length() > 255) {
            throw new IllegalArgumentException("O autor deve ter no máximo 255 caracteres.");
        }

        String isbnFinal = (isbn == null) ? "" : isbn.trim();

        return repository.adicionarLivro(titulo.trim(), autor.trim(), isbnFinal);
    }

    @Override
    public List<Livro> listarLivros() {
        return repository.listarLivros();
    }

    @Override
    public String atualizarLivro(int id, String titulo, String autor, String isbn, boolean disponivel) {
        Optional<Livro> livroOptional = repository.buscarPorId(id);
        if (livroOptional.isEmpty()) {
            return "Livro com ID " + id + " não encontrado.";
        }

        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O título não pode ser vazio.");
        }
        if (autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("O autor não pode ser vazio.");
        }

        Livro livroAtualizado = livroOptional.get()
                .comTitulo(titulo.trim())
                .comAutor(autor.trim())
                .comIsbn(isbn == null ? "" : isbn.trim())
                .comDisponivel(disponivel);

        if (repository.atualizarLivro(livroAtualizado)) {
            return "Livro " + id + " atualizado com sucesso.";
        }
        return "Erro ao atualizar o livro.";
    }

    @Override
    public String removerLivro(int id) {
        Optional<Livro> livroOptional = repository.buscarPorId(id);
        if (livroOptional.isEmpty()) {
            return "Livro com ID " + id + " não encontrado.";
        }
        if (repository.removerLivro(id)) {
            return "Livro com ID " + id + " removido com sucesso.";
        }
        return "Erro ao remover o livro.";
    }

    @Override
    public String alterarDisponibilidade(int id, boolean disponivel) {
        Optional<Livro> livroOptional = repository.buscarPorId(id);
        if (livroOptional.isEmpty()) {
            return "Livro com ID " + id + " não encontrado.";
        }
        Livro livroAtualizado = livroOptional.get().comDisponivel(disponivel);
        if (repository.atualizarLivro(livroAtualizado)) {
            String status = disponivel ? "disponível" : "indisponível";
            return "Livro " + id + " marcado como " + status + ".";
        }
        return "Erro ao alterar a disponibilidade do livro.";
    }
}
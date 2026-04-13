package br.edu.projeto.model;
import br.edu.projeto.model.Livro;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de acesso a dados para Livro.
 */
public interface LivroRepository {
    int adicionarLivro(String titulo, String autor, String isbn);
    boolean atualizarLivro(Livro livroAtualizado);
    boolean removerLivro(int id);
    List<Livro> listarLivros();
    Optional<Livro> buscarPorId(int id);
}
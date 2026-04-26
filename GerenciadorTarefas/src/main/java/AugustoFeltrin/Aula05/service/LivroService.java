package AugustoFeltrin.Aula05.service;

import java.util.List;

import AugustoFeltrin.Aula05.model.Livro;

public interface LivroService {
    void adicionar(String descricao);
    List<Livro> listarLivros();
    void lerLivro(int id);
    void remover(int id);
}

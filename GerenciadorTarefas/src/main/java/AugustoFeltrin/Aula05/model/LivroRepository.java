package AugustoFeltrin.Aula05.model;

import java.util.List;
import java.util.Optional;

public interface LivroRepository {
    void adicionar(String titulo);
    List<Livro> listarLivros();
    Optional<Livro> buscarPorID(int id);
    boolean atualizar(Livro livro); 
    boolean remover(int id);
} 


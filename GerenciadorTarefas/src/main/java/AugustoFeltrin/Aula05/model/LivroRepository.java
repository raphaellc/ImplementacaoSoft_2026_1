package AugustoFeltrin.Aula05.model;

import java.util.List;
import java.util.ArrayList;

public class LivroRepository {
    private final List<Livro> livros = new ArrayList<>();
    private int proximoID = 1;

    public void adicionar(String titulo){
        livros.add(new Livro(proximoID++, titulo, false));
    }

    public List<Livro> listarLivros(){
        return new ArrayList<>(livros);
    }

    public void lerLivro(int id){
        for(int i = 0; i < livros.size(); i++){
            Livro L = livros.get(i);
            if(L.id() == id){
                livros.set(i, new Livro(L.id(), L.titulo(), true));
                return;
            } 
        }
    }
}

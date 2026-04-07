package AugustoFeltrin.Aula05.service;
import AugustoFeltrin.Aula05.model.Livro;
import AugustoFeltrin.Aula05.model.LivroRepository;
import java.util.List;
import java.util.Optional;

public class LivroServiceImpl implements LivroService{
    private LivroRepository repository;

    public LivroServiceImpl(LivroRepository repository){
        this.repository = repository;
    }

    @Override
    public void adicionar(String descricao){
        if(descricao != null && !descricao.isBlank()){
            repository.adicionar(descricao);
        }
    }

    @Override
    public List<Livro> listarLivros(){
        return repository.listarLivros();
    }

    @Override 
     public void lerLivro(int id){
        lerLivro(id);
    }
   
}

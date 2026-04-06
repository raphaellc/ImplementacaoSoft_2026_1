package TarefaAula5;
import java.util.ArrayList;
import java.util.List;
public class LivroRepository {
    private List<Livro> livros = new ArrayList<>();
    private int id = 1;


    public boolean marcarComoAlugado(int id) {
        for (int i = 0; i < livros.size(); i++) {
            Livro livro = livros.get(i);
            if (livro.id() == id) {
                livros.set(i, livro.marcarComoAlugado());
                return true;
            }
        }
        return false;
    }

    public void AdicionarLivro(String titulo) {
        livros.add(new Livro(id++, titulo, false));
    }

    public List<Livro> listarLivros() {
        return new ArrayList<>(livros);
    }
}



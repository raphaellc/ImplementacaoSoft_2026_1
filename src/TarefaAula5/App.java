package TarefaAula5;
import TarefaAula5.LivroView;
import TarefaAula5.LivroRepository;
import TarefaAula5.LivroController;
import TarefaAula5.Livro;

public class App {
    public static void main(String[] args) {

        LivroRepository repository = new LivroRepository();

        LivroView view = new LivroView();

        LivroController controller = new LivroController(repository, view);

        controller.iniciar();
    }
}

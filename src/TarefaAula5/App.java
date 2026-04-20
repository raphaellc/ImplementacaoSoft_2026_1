package TarefaAula5;

class App {
    void main() {
        LivroRepository repository = new LivroRepository();
        LivroView view = new LivroView();
        LivroController controller = new LivroController(repository, view);

        controller.iniciar();
    }
}
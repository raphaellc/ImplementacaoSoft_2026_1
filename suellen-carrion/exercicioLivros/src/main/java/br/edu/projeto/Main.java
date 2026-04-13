package br.edu.projeto;

import br.edu.projeto.controller.LivroController;
import br.edu.projeto.repository.LivroRepository;
import br.edu.projeto.service.LivroService;
import br.edu.projeto.view.LivroView;

public class Main {

    public static void main(String[] args) {

        LivroRepository repository = new LivroRepository();
        LivroService service = new LivroService(repository);
        LivroView view = new LivroView();

        LivroController controller = new LivroController(service, view);

        controller.iniciar();
    }
}
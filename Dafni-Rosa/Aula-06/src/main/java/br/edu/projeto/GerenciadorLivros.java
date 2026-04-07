package br.edu.projeto;

import br.edu.projeto.controller.LivroController;
import br.edu.projeto.model.LivroRepository;
import br.edu.projeto.view.LivroView;
import br.edu.projeto.service.LivroService;
import br.edu.projeto.service.LivroServiceImpl;

public class GerenciadorLivros {
    public static void main(String[] args) {
        LivroView view = new LivroView();
        LivroRepository repository = new LivroRepository();
        LivroService service = new LivroServiceImpl(repository);
        LivroController controller = new LivroController(view, service);

        controller.iniciarGerenciadorLivros();
    }
}
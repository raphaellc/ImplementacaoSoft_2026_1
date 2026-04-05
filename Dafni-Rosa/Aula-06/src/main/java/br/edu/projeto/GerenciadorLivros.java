package br.edu.projeto;

import br.edu.projeto.controller.LivroController;
import br.edu.projeto.model.LivroRepository;
import br.edu.projeto.view.LivroView;

public class GerenciadorLivros {
    public static void main(String[] args) {
        LivroView view = new LivroView();
        LivroRepository repository = new LivroRepository();

        LivroController controller = new LivroController(view, repository);
        controller.iniciarGerenciadorLivros();
    }
}
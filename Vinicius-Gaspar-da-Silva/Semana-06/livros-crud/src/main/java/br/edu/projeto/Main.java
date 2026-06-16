package br.edu.projeto;

import br.edu.projeto.controller.LivroController;
import br.edu.projeto.model.LivroRepository;
import br.edu.projeto.view.LivroView;

public class Main {
    public static void main(String[] args) {
        LivroController app = new LivroController(new LivroRepository(), new LivroView());
        app.iniciar();
    }
}
package br.mvc.livros;

import br.mvc.livros.controller.LivroController;
import br.mvc.livros.model.LivroRepositorio;
import br.mvc.livros.view.LivroView;

public class App {

	public static void main(String[] args) {
		LivroRepositorio repositorio = new LivroRepositorio();
		LivroView view = new LivroView();
		LivroController controller = new LivroController(repositorio, view);

		controller.iniciar();
	}
}
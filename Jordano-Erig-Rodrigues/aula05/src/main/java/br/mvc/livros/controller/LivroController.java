package br.mvc.livros.controller;

import br.mvc.livros.model.LivroRepositorio;
import br.mvc.livros.view.LivroView;

public class LivroController {

	private final LivroRepositorio repositorio;
	private final LivroView view;

	public LivroController(LivroRepositorio repositorio, LivroView view) {
		this.repositorio = repositorio;
		this.view = view;
	}

	public void iniciar() {
		boolean rodando = true;

		while (rodando) {
			view.exibirMenu();
			String opcao = view.lerOpcao();

			switch (opcao) {
			case "1" -> {
				String descricao = view.lerDescricao();
				repositorio.adicionar(descricao);
				view.exibirMensagem("Livro adicionado com sucesso!");
			}
			case "2" -> view.exibirLivros(repositorio.listarTodas());
			case "3" -> {
				view.exibirLivros(repositorio.listarTodas());
				int id = view.lerId();
				boolean sucesso = repositorio.lendo(id);
				view.exibirMensagem(sucesso ? "Lendo livro" : "Livro não encontrado.");
			}
			case "4" -> {
				view.exibirLivros(repositorio.listarTodas());
				int id = view.lerId();
				boolean sucesso = repositorio.concluir(id);
				view.exibirMensagem(sucesso ? "Livro lido com sucesso!" : "Livro não encontrado.");
			}
			case "0" -> {
				view.exibirMensagem("Até logo!");
				rodando = false;
			}
			default -> view.exibirMensagem("Opção inválida. Tente novamente.");
			}
		}
	}
}

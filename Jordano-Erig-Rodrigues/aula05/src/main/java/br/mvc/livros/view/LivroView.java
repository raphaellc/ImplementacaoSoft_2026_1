package br.mvc.livros.view;

import br.mvc.livros.model.Livro;
import java.util.List;
import java.util.Scanner;

public class LivroView {

	private final Scanner scanner = new Scanner(System.in);

	public void exibirMenu() {
		System.out.println("\n=== Book List ===");
		System.out.println("1. Adicionar livro");
		System.out.println("2. Listar livros");
		System.out.println("3. Começar livro");
		System.out.println("4. Livro concluído");
		System.out.println("0. Sair");
		System.out.print("Escolha uma opção: ");
	}

	public String lerOpcao() {
		return scanner.nextLine().trim();
	}

	public String lerDescricao() {
		System.out.print("Digite o título do livro: ");
		return scanner.nextLine();
	}

	public int lerId() {
		System.out.print("Digite o ID do livro: ");
		try {
			return Integer.parseInt(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public void exibirLivros(List<Livro> livros) {
		if (livros.isEmpty()) {
			System.out.println("Nenhum livro cadastrado.");
			return;
		}
		System.out.println("\n--- Livros ---");
		livros.forEach(System.out::println);
	}

	public void exibirMensagem(String mensagem) {
		System.out.println(mensagem);
	}

}

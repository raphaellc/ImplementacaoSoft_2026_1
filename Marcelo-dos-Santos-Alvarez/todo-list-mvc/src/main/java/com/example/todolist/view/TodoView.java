package com.example.todolist.view;

import com.example.todolist.model.Todo;

import java.util.List;
import java.util.Scanner;

public class TodoView {
	private final Scanner scanner = new Scanner(System.in);

	public String showMenuAndGetInput() {
		System.out.println("\n=== menu ===");
		System.out.println("1 - Criar TODO");
		System.out.println("2 - Listar TODOs");
		System.out.println("3 - Completar TODO");
		System.out.println("0 - Sair");
		System.out.print("Opção escolhida: ");

		return scanner.nextLine();
	}

	public String askDescription() {
		System.out.print("Insira a descrição do TODO: ");

		return scanner.nextLine();
	}

	public int askId() {
		System.out.print("Insira o ID do TODO:");
		return Integer.parseInt(scanner.nextLine());
	}

	public void showTasks(List<Todo> todos) {
		if (todos.isEmpty()) {
			System.out.println("Nenhum TODO encontrado.");
			return;
		}

		todos.forEach(t -> System.out.printf("[%d] %s (%s)%n",
				t.id(),
				t.description(),
				t.done() ? "Completado" : "Pendente"));
	}

	public void showMessage(String message) {
		System.out.println(message);
	}
}

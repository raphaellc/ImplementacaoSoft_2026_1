package com.example.todolist.controller;

import com.example.todolist.repository.TodoRepository;
import com.example.todolist.view.TodoView;

public class TodoController {
	private final TodoRepository repository;
	private final TodoView view;

	public TodoController(TodoRepository repository, TodoView view) {
		this.repository = repository;
		this.view = view;
	}

	public void start() {
		while (true) {
			String input = view.showMenuAndGetInput();

			switch (input) {
				case "1" -> addTodo();
				case "2" -> listTodos();
				case "3" -> markAsDone();
				case "0" -> {
					return;
				}
				default -> view.showMessage("Opção inválida!");
			}
		}
	}

	private void addTodo() {
		String description = view.askDescription();

		repository.add(description);

		view.showMessage("TODO adicionado!");
	}

	private void listTodos() {
		view.showTasks(repository.findAll());
	}

	private void markAsDone() {
		int id = view.askId();

		repository.findById(id)
				.map(todo -> todo.markAsDone())
				.ifPresentOrElse(
						repository::update,
						() -> view.showMessage("TODO não encontrado!"));

		view.showMessage("TODO atualizado!");
	}
}

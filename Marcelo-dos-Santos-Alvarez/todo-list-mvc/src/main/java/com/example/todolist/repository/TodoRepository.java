package com.example.todolist.repository;

import com.example.todolist.model.Todo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TodoRepository {
	private final List<Todo> todos = new ArrayList<>();
	private int nextId = 1;

	public Todo add(String description) {
		Todo todo = new Todo(nextId++, description, false);

		todos.add(todo);

		return todo;
	}

	public List<Todo> findAll() {
		return List.copyOf(todos);
	}

	public Optional<Todo> findById(int id) {
		return todos.stream()
				.filter(t -> t.id() == id)
				.findFirst();
	}

	public void update(Todo updated) {
		todos.replaceAll(t -> t.id() == updated.id() ? updated : t);
	}
}

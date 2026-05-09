package com.example.todolist.model;

public record Todo(int id, String description, boolean done) {
	public Todo markAsDone() {
		return new Todo(id, description, true);
	}
}

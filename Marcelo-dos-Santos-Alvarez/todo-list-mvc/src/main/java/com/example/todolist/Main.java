package com.example.todolist;

import com.example.todolist.controller.TodoController;
import com.example.todolist.repository.TodoRepository;
import com.example.todolist.view.TodoView;

public class Main {
	public static void main(String[] args) {
		TodoRepository repository = new TodoRepository();
		TodoView view = new TodoView();
		TodoController controller = new TodoController(repository, view);

		controller.start();
	}
}

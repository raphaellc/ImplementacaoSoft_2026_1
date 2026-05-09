package com.gerenciadorlivros;

import com.gerenciadorlivros.repository.LivrosRepository;
import com.gerenciadorlivros.repository.LivrosRepositoryH2;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.gerenciadorlivros.api.LivrosHandler;
import com.sun.net.httpserver.HttpServer;
import com.gerenciadorlivros.service.LivrosService;
import com.gerenciadorlivros.service.LivrosServiceImpl;

public class GerenciadorLivros {
	public static void main(String[] args) {
		LivrosRepository repository = new LivrosRepositoryH2();
		LivrosService service = new LivrosServiceImpl(repository);

		HttpServer server = null;

		try {
			server = HttpServer.create(new InetSocketAddress(8080), 0);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		server.createContext("/api/livros", new LivrosHandler(service));
		server.setExecutor(null);
		server.start();

		IO.println("API rodando em http://localhost:8080/api/livros");
	}
}

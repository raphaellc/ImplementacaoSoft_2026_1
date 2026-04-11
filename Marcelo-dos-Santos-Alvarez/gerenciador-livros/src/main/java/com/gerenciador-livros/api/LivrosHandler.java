package com.gerenciadorlivros.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerenciadorlivros.service.LivrosService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class LivrosHandler implements HttpHandler {

	private static final int MAX_BODY_BYTES = 1024; // 1 KB
	private static final String ALLOWED_ORIGIN = System.getenv().getOrDefault("CORS_ORIGIN", "http://127.0.0.1:5500");

	private final LivrosService service;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public LivrosHandler(LivrosService service) {
		this.service = service;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String origin = exchange.getRequestHeaders().getFirst("Origin");

		if (origin != null && origin.equals(ALLOWED_ORIGIN)) {
			exchange.getResponseHeaders().set("Access-Control-Allow-Origin", origin);
		}

		exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
		exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

		String method = exchange.getRequestMethod();
		String path = exchange.getRequestURI().getPath();

		if ("OPTIONS".equals(method)) {
			exchange.sendResponseHeaders(204, -1);
			return;
		}

		if ("GET".equals(method)) {
			handleGet(exchange);
		} else if ("POST".equals(method)) {
			handlePost(exchange);
		} else {
			sendResponse(exchange, 405, "Método não permitido");
		}
	}

	private void handleGet(HttpExchange exchange) throws IOException {
		var livros = service.listarLivros();
		byte[] response = objectMapper.writeValueAsBytes(livros);

		exchange.getResponseHeaders().set("Content-Type", "application/json");
		exchange.sendResponseHeaders(200, response.length);

		try (OutputStream os = exchange.getResponseBody()) {
			os.write(response);
		}
	}

	private void handlePost(HttpExchange exchange) throws IOException {
		String body = readBody(exchange);

		if (body == null) {
			sendResponse(exchange, 400, "Corpo da requisição muito grande ou ausente");
			return;
		}

		String nome;

		try {
			JsonNode node = objectMapper.readTree(body);
			JsonNode campo = node.get("nome");

			if (campo == null || campo.isNull()) {
				sendResponse(exchange, 400, "Campo 'nome' é obrigatório");
				return;
			}

			nome = campo.asText().trim();
		} catch (Exception e) {
			sendResponse(exchange, 400, "JSON inválido");
			return;
		}

		if (nome.isBlank()) {
			sendResponse(exchange, 400, "O nome não pode ser vazio");
			return;
		}

		if (nome.length() > 255) {
			sendResponse(exchange, 400, "O nome deve ter no máximo 255 caracteres");
			return;
		}

		try {
			service.adicionarLivro(nome);

			sendResponse(exchange, 201, "Livro criado com sucesso");
		} catch (IllegalArgumentException e) {
			sendResponse(exchange, 400, e.getMessage());
		} catch (Exception e) {
			IO.println(e.getMessage());
			sendResponse(exchange, 500, "Erro interno ao criar livro");
		}
	}

	private String readBody(HttpExchange exchange) throws IOException {
		try (InputStream is = exchange.getRequestBody()) {
			byte[] buffer = new byte[MAX_BODY_BYTES + 1];
			int bytesRead = is.read(buffer);

			if (bytesRead > MAX_BODY_BYTES) {
				return null;
			}

			if (bytesRead <= 0) {
				return "";
			}

			return new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
		}
	}

	private void sendResponse(HttpExchange exchange, int status, String message) throws IOException {
		byte[] body = ("{\"mensagem\":\"" + message + "\"}")
				.getBytes(StandardCharsets.UTF_8);

		exchange.getResponseHeaders().set("Content-Type", "application/json");
		exchange.sendResponseHeaders(status, body.length);

		try (OutputStream os = exchange.getResponseBody()) {
			os.write(body);
		}
	}
}
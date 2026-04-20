package com.helloworldapi.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helloworldapi.service.HelloWorldService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HelloWorldHandler implements HttpHandler {
	private static final int MAX_BODY_BYTES = 1024; // 1 KB
	private static final String ALLOWED_ORIGIN = System.getenv().getOrDefault("CORS_ORIGIN", "http://127.0.0.1:5500");

	private final HelloWorldService service;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public HelloWorldHandler(HelloWorldService service) {
		this.service = service;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String origin = exchange.getRequestHeaders().getFirst("Origin");

		if (origin != null && origin.equals(ALLOWED_ORIGIN)) {
			exchange.getResponseHeaders().set("Access-Control-Allow-Origin", origin);
		}

		exchange.getResponseHeaders().set(
				"Access-Control-Allow-Methods",
				"GET, POST, PUT, DELETE, OPTIONS");
		exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

		String method = exchange.getRequestMethod();
		String path = exchange.getRequestURI().getPath();

		if ("OPTIONS".equals(method)) {
			exchange.sendResponseHeaders(204, -1);
			return;
		}

		if ("POST".equals(method)) {
			handlePost(exchange);
		} else if ("GET".equals(method)) {
			handleGet(exchange);
		} else {
			sendResponse(exchange, 405, "Método não permitido");
		}
	}

	private void handlePost(HttpExchange exchange) throws IOException {
		try {
			String message = service.postHelloWorld();

			sendResponse(exchange, 200, message);
		} catch (IllegalArgumentException e) {
			sendResponse(exchange, 400, e.getMessage());
		} catch (Exception e) {
			IO.println(e.getMessage());
			sendResponse(exchange, 500, "Erro interno");
		}
	}

	private void handleGet(HttpExchange exchange) throws IOException {
		try {
			String message = service.getHelloWorld();

			sendResponse(exchange, 200, message);
		} catch (IllegalArgumentException e) {
			sendResponse(exchange, 400, e.getMessage());
		} catch (Exception e) {
			IO.println(e.getMessage());
			sendResponse(exchange, 500, "Erro interno");
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
		byte[] body = ("{\"message\":\"" + message + "\"}")
				.getBytes(StandardCharsets.UTF_8);

		exchange.getResponseHeaders().set("Content-Type", "application/json");
		exchange.sendResponseHeaders(status, body.length);

		try (OutputStream os = exchange.getResponseBody()) {
			os.write(body);
		}
	}
}
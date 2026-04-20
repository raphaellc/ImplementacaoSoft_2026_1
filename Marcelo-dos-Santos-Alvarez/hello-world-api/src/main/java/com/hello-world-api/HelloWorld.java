package com.helloworldapi;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.helloworldapi.api.HelloWorldHandler;
import com.sun.net.httpserver.HttpServer;
import com.helloworldapi.service.HelloWorldService;
import com.helloworldapi.service.HelloWorldServiceImpl;

public class HelloWorld {
	public static void main(String[] args) {
		HelloWorldService service = new HelloWorldServiceImpl();

		HttpServer server = null;

		try {
			server = HttpServer.create(new InetSocketAddress(8080), 0);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		server.createContext("/api/hello-world", new HelloWorldHandler(service));
		server.setExecutor(null);
		server.start();

		IO.println("API rodando em http://localhost:8080/api/hello-world");
	}
}

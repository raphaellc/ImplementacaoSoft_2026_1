package com.login;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import java.net.URI;

public class Main {
    public static void main(String[] args) {
        // Onde a API vai escutar
        String baseUri = "http://localhost:8080/";
        
        // Avisa o Jersey para procurar as classes @Path no pacote controller
        ResourceConfig config = new ResourceConfig().packages("main.com.login.controller");
        
        System.out.println("Iniciando servidor na porta 8080...");
        GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUri), config);
        
        System.out.println("API pronta! Endpoint: " + baseUri + "login");
    }
}

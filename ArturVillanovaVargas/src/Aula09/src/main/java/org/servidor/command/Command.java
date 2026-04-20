package Aula09.src.main.java.org.servidor.command;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public interface Command {
    void execute(HttpExchange exchange) throws IOException;
}

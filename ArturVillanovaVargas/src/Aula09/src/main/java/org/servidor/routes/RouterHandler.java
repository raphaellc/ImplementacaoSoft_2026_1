package org.servidor.routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.servidor.command.Command;
import org.servidor.command.OlaMundoGetCommand;
import org.servidor.command.OlaMundoPostCommand;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RouterHandler implements HttpHandler {
    private final Map<String, Command> routes = new HashMap<>();

    public RouterHandler() {
        routes.put("GET:/api/ola-mundo", new OlaMundoGetCommand());
        routes.put("POST:/api/ola-mundo", new OlaMundoPostCommand());
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        String key = method + ":" + path;

        Command command = routes.get(key);

        if (command != null) {
            command.execute(exchange);
            return;
        }
        exchange.sendResponseHeaders(404, -1);
    }
}

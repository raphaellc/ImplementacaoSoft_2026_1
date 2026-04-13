package com.gerenciadortarefas.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Serve arquivos estáticos a partir de src/main/recursos/static/.
 * Qualquer rota não reconhecida cai em index.html (SPA-friendly).
 */
public class StaticHandler implements HttpHandler {

    private static final Path STATIC_DIR =
            Paths.get("src", "main", "recursos", "static");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String requestPath = exchange.getRequestURI().getPath();
        // "/" → index.html
        if (requestPath.equals("/")) {
            requestPath = "/index.html";
        }

        // Impede path traversal (ex: /../../../etc/passwd)
        Path filePath = STATIC_DIR.resolve(requestPath.substring(1)).normalize();
        if (!filePath.startsWith(STATIC_DIR.normalize())) {
            exchange.sendResponseHeaders(403, -1);
            return;
        }

        if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
            // Fallback para index.html (útil se houver navegação client-side)
            filePath = STATIC_DIR.resolve("index.html");
        }

        String contentType = detectContentType(filePath.toString());
        byte[] body = Files.readAllBytes(filePath);

        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, body.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body);
        }
    }

    private String detectContentType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=UTF-8";
        if (path.endsWith(".css"))  return "text/css; charset=UTF-8";
        if (path.endsWith(".js"))   return "application/javascript; charset=UTF-8";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".png"))  return "image/png";
        if (path.endsWith(".ico"))  return "image/x-icon";
        return "application/octet-stream";
    }
}

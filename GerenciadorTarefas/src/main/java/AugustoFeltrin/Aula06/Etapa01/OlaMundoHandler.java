package AugustoFeltrin.Aula06.Etapa01;

import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class OlaMundoHandler implements HttpHandler{
    @Override 
    public void handle(HttpExchange exchange) throws IOException {
        String response = "Olá mundo!";
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        
        try(OutputStream os = exchange.getResponseBody()){
            os.write(bytes);
        }
    }
}

package br.com.implementacao.helloworld.service;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import br.com.implementacao.helloworld.model.HelloResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class HelloService {
    public HelloResponse buildHello(String nome){
        String mensagem = "Hello, " + (nome != null ? nome : "World") + "!";
        String agora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        HelloResponse helloResponse = new HelloResponse(mensagem);
        helloResponse.setTimestamp(agora);
        return helloResponse;
    }    
}

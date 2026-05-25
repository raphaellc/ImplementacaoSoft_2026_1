package br.com.implementacao.helloworld.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import br.com.implementacao.helloworld.service.*;
import br.com.implementacao.helloworld.model.*;


@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class HelloControler {

    private final HelloService helloService;

    public HelloControler(HelloService helloService) {
        this.helloService = helloService;
    }

    // GET /api/hello -> Hello, World!
    @GetMapping(value="/")
    public ResponseEntity<HelloResponse> hello() {
        HelloResponse helloResponse = helloService.buildHello(null);
        return ResponseEntity.ok(helloResponse);
    }
    // GET /api/hello/{nome} -> Hello, {nome}!
    @GetMapping(value="/{nome}")
    public ResponseEntity<HelloResponse> helloNome(@PathVariable String nome) {
        HelloResponse helloResponse = helloService.buildHello(nome);
        return ResponseEntity.ok(helloResponse);
    }

}

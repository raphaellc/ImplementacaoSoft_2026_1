package AugustoFeltrin.Aula05.controller;

import AugustoFeltrin.Aula05.service.LivroService;
import AugustoFeltrin.Aula05.view.LivroView;

public class LivroController {
    private final LivroService service;
    private final LivroView view;
    
    public LivroController(LivroService service , LivroView view){
        this.service = service;
        this.view = view;
    }

    public void iniciar(){
        boolean rodando = true;
        while(rodando){
            String opcao = view.menu();

            switch (opcao) {
                case "1" -> service.adicionar(view.pedirTitulo());
                case "2" -> view.exibirLivros(service.listarLivros());
                case "3" -> service.lerLivro(view.pedirID());
                case "4" -> service.remover(view.pedirID());
                case "5" -> rodando = false;
                default -> System.out.println("Opção inválida!");
            }
        }
    }
    
}

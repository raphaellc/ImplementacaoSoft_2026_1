package AugustoFeltrin.Aula05.controller;

import AugustoFeltrin.Aula05.model.LivroRepository;
import AugustoFeltrin.Aula05.view.LivroView;

public class LivroController {
    private final LivroRepository model;
    private final LivroView view;
    
    public LivroController(LivroRepository model, LivroView view){
        this.model = model;
        this.view = view;
    }

    public void iniciar(){
        boolean rodando = true;
        while(rodando){
            String opcao = view.menu();

            switch (opcao) {
                case "1" -> model.adicionar(view.pedirTitulo());
                case "2" -> view.exibirLivros(model.listarLivros());
                case "3" -> model.lerLivro(view.pedirID());
                case "4" -> rodando = false;
                default -> System.out.println("Opção inválida!");
            }
        }
    }
    
}

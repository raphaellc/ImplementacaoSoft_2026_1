package AugustoFeltrin.Aula05;

import AugustoFeltrin.Aula05.controller.LivroController;
import AugustoFeltrin.Aula05.view.LivroView;
import AugustoFeltrin.Aula05.model.LivroRepository;

public class Main {
    public static void main(String[] args) {
        new LivroController(new LivroRepository(), new LivroView()).iniciar();;
    }    
}

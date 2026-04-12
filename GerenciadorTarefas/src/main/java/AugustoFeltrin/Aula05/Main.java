package AugustoFeltrin.Aula05;

import AugustoFeltrin.Aula05.controller.LivroController;
import AugustoFeltrin.Aula05.view.LivroView;
import AugustoFeltrin.Aula05.model.LivroRepository;
import AugustoFeltrin.Aula05.model.LivroRepositoryH2;
import AugustoFeltrin.Aula05.service.LivroService;
import AugustoFeltrin.Aula05.service.LivroServiceImpl;

public class Main {
    public static void main(String[] args) {
        LivroView view = new LivroView();
        LivroRepository repo = new LivroRepositoryH2();
        LivroService service = new LivroServiceImpl(repo);
        new LivroController(service, view).iniciar();    }    
}
package AugustoFeltrin.Aula04;

import AugustoFeltrin.Aula04.controller.TarefaController;
import AugustoFeltrin.Aula04.model.TarefaRepository;
import AugustoFeltrin.Aula04.view.TarefaView;

public class Main {
    public static void main(String[] args) {
        new TarefaController(new TarefaRepository(), new TarefaView()).iniciar();
    }
}
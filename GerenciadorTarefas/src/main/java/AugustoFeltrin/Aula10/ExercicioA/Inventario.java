package AugustoFeltrin.Aula10.ExercicioA;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Inventario {
    private List<Item> itens = new ArrayList<>();

    public void adicionarItem(Item item){
        this.itens.add(item);
    }

    public Optional<Item> buscarItem(String nome){
        return itens.stream().filter(i -> i.nome().equals(nome)).findFirst();
    }
}


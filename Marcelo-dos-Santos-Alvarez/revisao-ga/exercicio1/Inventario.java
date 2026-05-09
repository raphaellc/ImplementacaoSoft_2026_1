import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Inventario {
	private List<Item> itens = new ArrayList<>();

	public Inventario() {
		itens.add(new Item("Dragonslayer", 1));
		itens.add(new Item("Poção de Cura", 5));
	}

	public Optional<Item> buscarItem(String nome) {
		return itens.stream()
				.filter(item -> item.nome().equalsIgnoreCase(nome))
				.findFirst();
	}
}

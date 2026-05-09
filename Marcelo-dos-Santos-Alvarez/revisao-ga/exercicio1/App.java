public class App {
	public static void main(String[] args) {
		Inventario inventario = new Inventario();

		inventario.buscarItem("Poção de Cura")
				.ifPresentOrElse(
						item -> IO.println(
								"Item: " + item.nome() +
										" | Quantidade: " + item.quantidade()),
						() -> IO.println("Item não encontrado"));
	}
}

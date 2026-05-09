public class ExameController {
	private ExameService service;

	public ExameController(
			ExameService service) {
		this.service = service;
	}

	public void buscarExame() {
		try {
			int id = Integer.parseInt(
					IO.readln("Digite o ID do exame: "));

			Exame exame = service.obterExame(id);

			IO.println(exame.toString());
		} catch (RuntimeException e) {
			IO.println(
					"Erro: " + e.getMessage());
		}
	}
}

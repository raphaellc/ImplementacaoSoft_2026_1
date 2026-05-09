public class ClinicaController {
	public void iniciar() {
		int opcao;

		do {
			IO.println("\n=== CLÍNICA ===");
			IO.println("1 - Agendar consulta");
			IO.println("2 - Listar consultas");
			IO.println("0 - Sair");

			opcao = Integer.parseInt(
					IO.readln("Escolha uma opção: "));

			switch (opcao) {
				case 1 -> agendarConsulta();
				case 2 -> listarConsultas();
				case 0 -> IO.println("Saindo...");
				default -> IO.println("Opção inválida");
			}

		} while (opcao != 0);
	}

	private void agendarConsulta() {
		IO.println("Consulta agendada");
	}

	private void listarConsultas() {
		IO.println("Listando consultas");
	}
}

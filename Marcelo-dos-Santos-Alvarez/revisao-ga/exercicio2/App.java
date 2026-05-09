public class App {
	public static void main(String[] args) {
		PacienteService service = new PacienteService();

		Paciente paciente1 = new Paciente(
				"João Silva",
				"12345678900");
		Paciente paciente2 = new Paciente(
				"Maria Souza",
				"12345678900");

		service.cadastrarPaciente(paciente1);
		service.cadastrarPaciente(paciente2);
	}
}

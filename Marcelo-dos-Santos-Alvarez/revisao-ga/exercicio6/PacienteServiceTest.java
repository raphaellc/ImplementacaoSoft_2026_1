public class PacienteServiceTest {
	public static void main(String[] args) {
		PacienteRepositoryFake repository = new PacienteRepositoryFake();
		PacienteService service = new PacienteService(repository);

		Paciente paciente1 = new Paciente(
				"Ana",
				"12345678900");
		Paciente paciente2 = new Paciente(
				"Maria",
				"12345678900");

		service.cadastrarPaciente(paciente1);
		service.cadastrarPaciente(paciente2);
	}
}

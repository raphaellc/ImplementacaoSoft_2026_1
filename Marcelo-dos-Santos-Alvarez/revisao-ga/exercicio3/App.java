import java.util.Optional;

public class App {
	public static void main(String[] args) {
		Paciente paciente = new Paciente(
				"Carlos",
				Optional.empty());

		String emailFinal = paciente.getEmail()
				.orElse("sem-email@clinica.local");

		IO.println("Paciente: " + paciente.getNome());
		IO.println("Email: " + emailFinal);
	}
}

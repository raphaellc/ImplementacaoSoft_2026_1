import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PacienteRepository {
	private List<Paciente> pacientes = new ArrayList<>();

	public Optional<Paciente> buscarPorCpf(String cpf) {
		return pacientes.stream()
				.filter(p -> p.cpf().equals(cpf))
				.findFirst();
	}

	public void salvar(Paciente paciente) {
		pacientes.add(paciente);
	}
}

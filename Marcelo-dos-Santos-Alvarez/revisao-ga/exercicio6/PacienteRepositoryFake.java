import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PacienteRepositoryFake
		implements PacienteRepository {
	private List<Paciente> pacientes = new ArrayList<>();

	@Override
	public Optional<Paciente> buscarPorCpf(
			String cpf) {
		return pacientes.stream()
				.filter(p -> p.cpf().equals(cpf))
				.findFirst();
	}

	@Override
	public void salvar(Paciente paciente) {
		pacientes.add(paciente);

		IO.println(
				"Paciente salvo: " +
						paciente.nome());
	}
}

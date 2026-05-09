import java.util.Optional;

public interface PacienteRepository {
	Optional<Paciente> buscarPorCpf(String cpf);

	void salvar(Paciente paciente);
}

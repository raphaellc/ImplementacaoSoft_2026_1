public class PacienteService {
	private PacienteRepository repository = new PacienteRepository();

	public void cadastrarPaciente(Paciente p) {
		if (repository.buscarPorCpf(p.cpf()).isPresent()) {
			throw new IllegalArgumentException("CPF já cadastrado");
		}

		repository.salvar(p);
	}
}

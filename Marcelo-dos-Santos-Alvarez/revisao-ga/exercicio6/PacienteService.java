public class PacienteService {
	private PacienteRepository repository;

	public PacienteService(
			PacienteRepository repository) {
		this.repository = repository;
	}

	public void cadastrarPaciente(Paciente p) {
		if (repository.buscarPorCpf(
				p.cpf()).isPresent()) {

			throw new IllegalArgumentException(
					"CPF já cadastrado");
		}

		repository.salvar(p);
	}
}

public class ExameServiceImpl
		implements ExameService {

	private ExameRepository repository;

	public ExameServiceImpl(
			ExameRepository repository) {
		this.repository = repository;
	}

	@Override
	public Exame obterExame(int id) {
		return repository.buscarPorId(id)
				.orElseThrow(
						() -> new RuntimeException(
								"Exame não encontrado"));
	}
}

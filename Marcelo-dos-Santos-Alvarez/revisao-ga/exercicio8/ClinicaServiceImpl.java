import java.time.LocalDate;

public class ClinicaServiceImpl
		implements ClinicaService {

	private ClinicaRepository repository;

	public ClinicaServiceImpl(
			ClinicaRepository repository) {
		this.repository = repository;
	}

	@Override
	public Consulta agendar(
			Consulta consulta) {
		if (consulta.data()
				.isBefore(LocalDate.now())) {

			throw new IllegalArgumentException(
					"Data inválida");
		}

		return repository.salvar(consulta);
	}
}

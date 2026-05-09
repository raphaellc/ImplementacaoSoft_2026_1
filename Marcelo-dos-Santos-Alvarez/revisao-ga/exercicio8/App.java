import java.time.LocalDate;

public class App {
	public static void main(String[] args) {
		ClinicaRepository repository = new ClinicaRepositoryImpl();
		ClinicaService service = new ClinicaServiceImpl(repository);
		Consulta consulta = new Consulta(
				"Carlos",
				LocalDate.now().plusDays(2));

		Consulta consultaSalva = service.agendar(consulta);

		IO.println(
				consultaSalva.toString());
	}
}

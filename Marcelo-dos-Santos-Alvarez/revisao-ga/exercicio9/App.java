public class App {
	public static void main(String[] args) {
		ExameRepository repository = new ExameRepositoryImpl();
		ExameService service = new ExameServiceImpl(repository);
		ExameController controller = new ExameController(service);

		controller.buscarExame();
	}
}

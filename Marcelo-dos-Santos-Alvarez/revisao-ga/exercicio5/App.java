public class App {
	public static void main(String[] args) {
		StyleGuideRepositoryImpl repository = new StyleGuideRepositoryImpl();
		StyleGuideService service = new StyleGuideService(repository);
		// Refatorei para usar IO.readln invés!
		StyleGuideController controller = new StyleGuideController(service);

		controller.iniciar();
	}
}

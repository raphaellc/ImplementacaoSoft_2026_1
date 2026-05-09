public class StyleGuideService {
	private StyleGuideRepository repository;

	public StyleGuideService(StyleGuideRepository repository) {
		this.repository = repository;
	}

	public void cadastrarGuia(String guia) {
		repository.salvar(guia);
	}
}

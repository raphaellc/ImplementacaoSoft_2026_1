public class StyleGuideController {
	private StyleGuideService service;

	public StyleGuideController(StyleGuideService service) {
		this.service = service;
	}

	public void iniciar() {
		String guia = IO.readln(
				"Digite o nome do guia: ");

		service.cadastrarGuia(guia);
	}
}

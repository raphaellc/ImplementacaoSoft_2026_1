public class StyleGuideRepositoryImpl implements StyleGuideRepository {
	@Override
	public void salvar(String guia) {
		IO.println("Guia salvo: " + guia);
	}
}

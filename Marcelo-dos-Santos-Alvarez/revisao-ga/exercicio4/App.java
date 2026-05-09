public class App {
	public static void main(String[] args) {
		CorrecaoRepository repo = new CorrecaoRepositoryImpl();
		CorrecaoService service = new CorrecaoService(repo);

		service.corrigir("Resposta do aluno");
	}
}

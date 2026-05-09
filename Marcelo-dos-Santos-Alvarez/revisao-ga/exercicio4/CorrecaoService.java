public class CorrecaoService {
	private final CorrecaoRepository repo;

	public CorrecaoService(CorrecaoRepository repo) {
		this.repo = repo;
	}

	public void corrigir(String resposta) {
		repo.salvar(resposta);
	}
}

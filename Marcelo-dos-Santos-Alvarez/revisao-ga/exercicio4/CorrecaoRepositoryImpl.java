public class CorrecaoRepositoryImpl implements CorrecaoRepository {
	@Override
	public void salvar(String resposta) {
		IO.println("Correção salva: " + resposta);
	}
}

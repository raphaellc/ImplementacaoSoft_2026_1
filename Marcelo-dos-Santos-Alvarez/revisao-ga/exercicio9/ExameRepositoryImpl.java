import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExameRepositoryImpl
		implements ExameRepository {

	private List<Exame> exames = new ArrayList<>();

	public ExameRepositoryImpl() {
		exames.add(
				new Exame(
						1,
						"Hemograma",
						"Normal"));
	}

	@Override
	public Optional<Exame> buscarPorId(
			int id) {
		return exames.stream()
				.filter(e -> e.id() == id)
				.findFirst();
	}
}

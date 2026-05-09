import java.util.Optional;

public interface ExameRepository {
	Optional<Exame> buscarPorId(int id);
}

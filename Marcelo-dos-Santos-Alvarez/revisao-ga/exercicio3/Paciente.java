import java.util.Optional;

public class Paciente {
	private String nome;
	private Optional<String> email;

	public Paciente(String nome, Optional<String> email) {
		this.nome = nome;
		this.email = email;
	}

	public String getNome() {
		return nome;
	}

	public Optional<String> getEmail() {
		return email;
	}
}

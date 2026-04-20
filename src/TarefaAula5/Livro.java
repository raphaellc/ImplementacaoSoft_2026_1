package TarefaAula5;

public record Livro (int id, String Titulo, boolean Alugado) {
    public Livro marcarComoAlugado() {
        return new Livro (id, Titulo, true);
    }
}
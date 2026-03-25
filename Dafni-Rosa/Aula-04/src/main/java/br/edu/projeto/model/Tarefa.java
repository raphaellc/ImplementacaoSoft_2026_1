package br.edu.projeto.model;

public record Tarefa(int id, String descricao, boolean concluida) {
 
    /**
     * Construtor compacto para validação dos dados de entrada.
     */
    public Tarefa {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("A descrição da tarefa não pode ser vazia.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("O ID da tarefa deve ser maior que zero.");
        }
    }
 
    /**
     * Retorna uma nova Tarefa marcada como concluída.
     * Necessário pois Records são imutáveis.
     */
    public Tarefa marcarComoConcluida() {
        return new Tarefa(this.id, this.descricao, true);
    }
 
    /**
     * Representação formatada para exibição no console.
     */
    public String exibir() {
        String status = concluida ? "[✓]" : "[ ]";
        return String.format("%s #%d - %s", status, id, descricao);
    }
}
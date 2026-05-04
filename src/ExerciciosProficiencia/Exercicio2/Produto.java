package ExerciciosProficiencia.Exercicio2;

public class Produto {
    private float preco;
    private int estoque;

    public Produto(float preco, int estoque) {
        this.preco = (preco < 0) ? 0 : preco;
        this.estoque = (estoque < 0) ? 0 : estoque;
    }

    public float getPreco() {
        return preco;
    }

    public void setPreco(float novoPreco) {
        if (novoPreco < 0) {
            IO.println("Erro: O preço não pode ser negativo. Operação cancelada.");
        } else {
            this.preco = novoPreco;
        }
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int novaQuantidade) {
        if (novaQuantidade < 0) {
            IO.println("Erro: O estoque não pode ser negativo. Operação cancelada.");
        } else {
            this.estoque = novaQuantidade;
        }
    }
}


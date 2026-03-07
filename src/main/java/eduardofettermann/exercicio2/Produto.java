package eduardofettermann.exercicio2;

public class Produto {
    private float preco;
    private int estoque;

    public Produto(float preco, int estoque) {
        this.preco = Math.max(preco, 0);
        this.estoque = Math.max(estoque, 0);
    }

    public float getPreco() {
        return preco;
    }

    public void setPreco(float preco) {
        if (preco > 0) {
            this.preco = preco;
        } else {
            System.out.println("O preço não pode ser negativo!");
        }
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        if (estoque > 0) {
            this.estoque = estoque;
        } else {
            System.out.println("O estoque não pode ser negativo!");
        }
    }
}

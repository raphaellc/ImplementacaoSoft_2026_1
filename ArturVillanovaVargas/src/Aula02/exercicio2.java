public class exercicio2 {
    public static void main(String[] args) {
        var notebook = new Produto(4800, 2000);

        System.out.printf("--- Valores Iniciais do Produto ---\n" +
                "Preço: R$ %s\n" +
                "Estoque: %s unidades%n", notebook.getPreco(), notebook.getEstoque());
        try {
            notebook.setPreco(6000);
            notebook.setEstoque(notebook.getEstoque() - 1);

            System.out.printf("\n--- Valores Atualizados do Produto ---\n" +
                    "Preço: R$ %s\n" +
                    "Estoque: %s unidades%n", notebook.getPreco(), notebook.getEstoque());

            notebook.setPreco(-2);
            notebook.setEstoque(-1000);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}

class Produto {
    private double preco;
    private int estoque;

    public Produto(double preco, int estoque) {
        this.preco = preco > 0 ? preco : 0;
        this.estoque = estoque > 0 ? estoque : 0;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double novoPreco) {
        if(novoPreco < 0)
            throw new IllegalArgumentException("O preço não pode ser negativo");

        this.preco = novoPreco;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int novaQuantidade) {
        if(novaQuantidade < 0)
            throw new IllegalArgumentException("A quantidade não pode ser negativa");

        this.estoque = novaQuantidade;
    }
}


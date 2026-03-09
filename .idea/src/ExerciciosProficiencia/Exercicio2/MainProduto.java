package Exercicio2;

public class MainProduto {
    public static void main(String[] args) {
        Produto produto = new Produto(8000.00f, 15);

        IO.println("Valores Iniciais do Produto: ");
        IO.println("Preço: R$ " + produto.getPreco());
        IO.println("Estoque: " + produto.getEstoque());


        produto.setPreco(8550.50f);
        produto.setEstoque(14);

        IO.println("Valores Atualizados do Produto: ");
        IO.println("Preço: R$ " + produto.getPreco());
        IO.println("Estoque: " + produto.getEstoque());
        IO.println("");

        IO.println("Atualizando para valores negativos: ");
        produto.setPreco(-500.00f);
        produto.setEstoque(-5);

        IO.println("");
        IO.println("Valores Atualizados após tentaitva de valores negativos: ");
        IO.println("Preço: R$ " + produto.getPreco());
        IO.println("Estoque: " + produto.getEstoque());
    }
}
package aula02;

public class Exercicio02 {

    static class Produto {

        private float preco;
        private int estoque;

        public Produto(float preco, int estoque) {
            this.preco   = preco   < 0 ? 0 : preco;
            this.estoque = estoque < 0 ? 0 : estoque;
        }

        public float getPreco() {
            return preco;
        }

        public void setPreco(float novoPreco) {
            if (novoPreco < 0)
                System.out.println("Erro: preco nao pode ser negativo");
            else
                this.preco = novoPreco;
        }

        public int getEstoque() {
            return estoque;
        }

        public void setEstoque(int novaQuantidade) {
            if (novaQuantidade < 0)
                System.out.println("Erro: estoque nao pode ser negativo");
            else
                this.estoque = novaQuantidade;
        }
    }

    public static void main(String[] args) {
        Produto notebook = new Produto(8000.00f, 15);

        System.out.println("Valores iniciais");
        System.out.printf("Preco: R$ %.2f%n", notebook.getPreco());
        System.out.println("Estoque: " + notebook.getEstoque());

        notebook.setPreco(8550.50f);
        notebook.setEstoque(14);

        System.out.println("\nValores atualizados");
        System.out.printf("Preco: R$ %.2f%n", notebook.getPreco());
        System.out.println("Estoque: " + notebook.getEstoque());

        System.out.println("\nValores invalidos");
        notebook.setPreco(-100.00f);
        notebook.setEstoque(-5);
        System.out.printf("Preco: R$ %.2f%n", notebook.getPreco());
        System.out.println("Estoque: " + notebook.getEstoque());
    }
}
package eduardofettermann.exercicio2;

public class Main {
    public static void main(String[] args) {
        Produto gamerNotebook = new Produto(1200, 15);

        printProductValues(gamerNotebook);

        gamerNotebook.setEstoque(14);
        gamerNotebook.setPreco(8550.50f);

        printProductValues(gamerNotebook);

        gamerNotebook.setEstoque(-1);
        gamerNotebook.setPreco(-1000.00f);

        printProductValues(gamerNotebook);
    }

    public static void printProductValues(Produto produto) {
        System.out.println();
        System.out.printf("""
                --- Valores Atualizados do Produto ---
                Preço: R$ %.2f
                Estoque: %d unidades
                %n""", produto.getPreco(), produto.getEstoque());
    }
}

package Aula02;

public class ProdutoMain {
    public static void main(String[] args) {
        Produto notebook =  new Produto(2000, 500);
        System.out.println("-- Valores Inicias do Produto --");
        System.out.println("Preço: R$" + notebook.getPreco());
        System.out.println("Estoque: " + notebook.getEstoque());
    
        notebook.setEstoque(20);
        notebook.setPreco(1500);
        System.out.println("ALERTA SUPER PROMOÇÃO! Últimos " + notebook.getEstoque() + " notebooks do estoque. \nPromoção de 50%: R$" + notebook.getPreco() );
    
        notebook.setEstoque(-659);
        notebook.setPreco(-1000);
    }    
}

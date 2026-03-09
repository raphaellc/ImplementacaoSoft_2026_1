package Aula02;

public class Produto {
    private float preco;
    private int estoque;

    public Produto(float preco, int estoque){
        if(preco >= 0){
            this.preco = preco;
        } else{
            this.preco = 0;
            System.out.println("Preço inicial negativo. Definido como 0.");
        }

        if(estoque >= 0){
            this.estoque = estoque;
        } else {
            this.estoque = 0;
            System.out.println("Estoque inicial negativo. Definido como 0");
        }
    }

    public float getPreco(){
        return preco;
    }

    public void setPreco(float novoPreco){
        if(novoPreco >= 0){
            this.preco = novoPreco;
        } else {
            System.out.println("Erro! Preço não pode ser negativo");
        }
    }

    public int getEstoque(){
        return estoque;
    }

    public void setEstoque(int novaQuantidade){
        if(novaQuantidade >= 0){
            this.estoque = novaQuantidade;
        } else {
            System.out.println("Erro! Estoque não pode ser negativo");
        }
    }
}

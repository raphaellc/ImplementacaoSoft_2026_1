import java.util.Random;
import java.util.Scanner;

class Produto {
    private float preco;
    private int estoque;

    public Produto(float preco, int estoque) {
        if (preco < 0) {
            this.preco = 0;
        } else {
            this.preco = preco;
        }

        if (estoque < 0) {
            this.estoque = 0;
        } else {
            this.estoque = estoque;
        }
    }

    public float getPreco() {
        return preco;
    }

    public void setPreco(float novoPreco) {
        if (novoPreco < 0) {
            System.out.println("Erro: preço não pode ser negativo.");
        } else {
            this.preco = novoPreco;
        }
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int novaQuantidade) {
        if (novaQuantidade < 0) {
            System.out.println("Erro: estoque não pode ser negativo.");
        } else {
            this.estoque = novaQuantidade;
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // =========================
        // EXERCÍCIO 1 - TABULEIRO
        // =========================
        System.out.println("Digite n (linhas): ");
        int n = sc.nextInt();

        System.out.println("Digite m (colunas): ");
        int m = sc.nextInt();

        char[] letras = new char[5];
        System.out.println("Digite 5 letras:");
        for (int i = 0; i < 5; i++) {
            letras[i] = sc.next().charAt(0);
        }

        char[][] tabuleiro = new char[n][m];
        Random rand = new Random();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                tabuleiro[i][j] = letras[rand.nextInt(5)];
                System.out.print(tabuleiro[i][j] + " ");
            }
            System.out.println();
        }

        // =========================
        // EXERCÍCIO 2 - PRODUTO
        // =========================
        Produto p = new Produto(8000, 15);

        System.out.println("\n--- Valores Iniciais ---");
        System.out.println("Preço: R$ " + p.getPreco());
        System.out.println("Estoque: " + p.getEstoque());

        p.setPreco(8550.50f);
        p.setEstoque(14);

        System.out.println("\n--- Valores Atualizados ---");
        System.out.println("Preço: R$ " + p.getPreco());
        System.out.println("Estoque: " + p.getEstoque());

        // Teste inválido
        p.setPreco(-10);
        p.setEstoque(-5);

        // =========================
        // EXERCÍCIO 3
        // =========================
        System.out.println("\nDigite números (0 para parar):");
        int num;
        do {
            num = sc.nextInt();
            if (num >= 5 && num <= 15) {
                System.out.println("Dentro do intervalo: " + num);
            }
        } while (num != 0);

        // =========================
        // EXERCÍCIO 4
        // =========================
        int[] vetor1 = new int[10];
        int soma = 0;

        for (int i = 0; i < 10; i++) {
            vetor1[i] = 20 + rand.nextInt(31); // 20 a 50
            soma += vetor1[i];
        }

        System.out.println("\nSoma: " + soma);

        // =========================
        // EXERCÍCIO 5
        // =========================
        int[] vetor2 = new int[10];

        System.out.println("\nDigite 10 números:");
        for (int i = 0; i < 10; i++) {
            vetor2[i] = sc.nextInt();
        }

        System.out.println("Invertido:");
        for (int i = 9; i >= 0; i--) {
            System.out.print(vetor2[i] + " ");
        }

        int maior = vetor2[0];
        int menor = vetor2[0];

        for (int i = 1; i < 10; i++) {
            if (vetor2[i] > maior) maior = vetor2[i];
            if (vetor2[i] < menor) menor = vetor2[i];
        }

        System.out.println("\nMaior: " + maior);
        System.out.println("Menor: " + menor);

        // Rotação à esquerda
        int primeiro = vetor2[0];
        for (int i = 0; i < 9; i++) {
            vetor2[i] = vetor2[i + 1];
        }
        vetor2[9] = primeiro;

        System.out.println("Rotação:");
        for (int i = 0; i < 10; i++) {
            System.out.print(vetor2[i] + " ");
        }

        sc.close();
    }
}
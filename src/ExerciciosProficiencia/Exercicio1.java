package ExerciciosProficiencia;

import java.util.Random;

public class Exercicio1 {
    public static void main(String[] args) {
        Random random = new Random();

        IO.println("Digite o número de linhas (n):");
        int n = Integer.parseInt(IO.readln());

        IO.println("Digite o número de colunas (m):");
        int m = Integer.parseInt(IO.readln());

        if (n * m < 5) {
            IO.println("Erro: o tabuleiro nao tem 5 letras");
            return;
        }

        char[][] tabuleiro = new char[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                tabuleiro[i][j] = '-';
            }
        }
        char[] letras = new char[5];
        IO.println("Digite 5 letras *pressione a tecla ENTER após cada letra*:");

        for (int i = 0; i < 5; i++) {
            IO.println("Letra " + (i + 1) + ":");
            String entrada = IO.readln();
            letras[i] = entrada.charAt(0);
        }

        int letrasInseridas = 0;
        while (letrasInseridas < 5) {
            int linhaAleatoria = random.nextInt(n);
            int colunaAleatoria = random.nextInt(m);

            if (tabuleiro[linhaAleatoria][colunaAleatoria] == '-') {
                tabuleiro[linhaAleatoria][colunaAleatoria] = letras[letrasInseridas];
                letrasInseridas++;
            }
        }

        IO.println("Tabuleiro: ");
        for (int i = 0; i < n; i++) {
            String linhaImpressao = "";
            for (int j = 0; j < m; j++) {
                linhaImpressao += tabuleiro[i][j] + " ";
            }
            IO.println(linhaImpressao);
        }
    }
}
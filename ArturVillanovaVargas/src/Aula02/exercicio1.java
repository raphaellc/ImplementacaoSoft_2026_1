import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class exercicio1 {
    public static void main(String[] args) {

        var scanner = new Scanner(System.in);

        char[] letras = new char[5];

        for (int i = 0; i < letras.length; i++) {
            System.out.print("Digite a "+(i+1)+"a letra:");
            letras[i] = scanner.nextLine().charAt(0);
        }

        char[][] tabuleiro = criarTabuleiro(5, 5, letras);

        System.out.println(Arrays.deepToString(tabuleiro).replace("],","],\n"));
    }

    public static char[][] criarTabuleiro(int qtdLinhas, int qtdColunas, char[] letras) {

        var tabuleiro = new char[qtdLinhas][qtdColunas];
        for (int i = 0; i < tabuleiro.length; i++) {
            for (int j = 0; j < tabuleiro[i].length; j++) {
                tabuleiro[i][j] = sortearLetra(letras);
            }
        }
        return tabuleiro;
    }

    public static char sortearLetra(char[] letras) {
        var rand = new Random();
        return letras[rand.nextInt(letras.length)];
    }
}

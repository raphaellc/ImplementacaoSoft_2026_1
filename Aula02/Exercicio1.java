package Aula02;

import java.util.Scanner;
import java.util.Random;

public class Exercicio1 {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Random random = new Random();

        System.out.print("Número de linhas (n): ");
        int n = scan.nextInt();
        System.out.print("Número de colunas (m): ");
        int m = scan.nextInt();

        System.out.print("Digite as 5 letras (sem espaços): ");
        String entrada = scan.next();

        if (entrada.length() != 5) {
            System.out.println("Erro: Informe exatamente 5 letras.");
            return;
        }

        char[] letras = entrada.toCharArray();

        System.out.println("Tabuleiro Gerado: ");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int indiceSorteado = random.nextInt(5);
                System.out.print(letras[indiceSorteado] + " ");
            }
            System.out.println();
        }
        scan.close();
    }
}

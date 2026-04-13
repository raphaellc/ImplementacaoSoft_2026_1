package aula02;

import java.util.Random;
import java.util.Scanner;

public class Exercicio01 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Random rand = new Random();

        System.out.print("Linhas: ");
        int n = sc.nextInt();

        System.out.print("Colunas: ");
        int m = sc.nextInt();

        char[] letras = new char[5];
        for (int i = 0; i < 5; i++) {
            System.out.print("Letra " + (i + 1) + ": ");
            letras[i] = sc.next().charAt(0);
        }

        System.out.println();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++)
                System.out.print(letras[rand.nextInt(5)] + " ");
            System.out.println();
        }

        sc.close();
    }
}

package aula02;

import java.util.Scanner;

public class Exercicio05 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int[] v = new int[10];

        for (int i = 0; i < v.length; i++) {
            System.out.print("Valor " + (i + 1) + ": ");
            v[i] = sc.nextInt();
        }

        System.out.print("\nInvertido: ");
        for (int i = v.length - 1; i >= 0; i--)
            System.out.print(v[i] + " ");

        int maior = v[0], menor = v[0];
        for (int x : v) {
            if (x > maior) maior = x;
            if (x < menor) menor = x;
        }
        System.out.println("\nMaior: " + maior + " | Menor: " + menor);

        System.out.print("\nRotacao: ");
        int primeiro = v[0];
        for (int i = 0; i < v.length - 1; i++)
            v[i] = v[i + 1];
        v[v.length - 1] = primeiro;

        for (int x : v)
            System.out.print(x + " ");

        sc.close();
    }
}
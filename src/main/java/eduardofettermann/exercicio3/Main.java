package eduardofettermann.exercicio3;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Esse programa imprime os números entre 5 e 15 que o usuário digita.");
        askUserForNumbers(scanner);
    }

    private static void askUserForNumbers(Scanner scanner) {
        System.out.println("Digite um número inteiro ou '0' para sair:");
        int number = scanner.nextInt();
        if (number == 0) {
            System.out.println("Programa encerrado.");
            System.out.println();
            return;
        }
        printNumberIfBetweenRange(number, 5, 15);
        askUserForNumbers(scanner);
    }

    private static void printNumberIfBetweenRange(int number, int min, int max) {
        if (number > min && number < max) {
            System.out.println("O número " + number + " está entre 5 e 15.");
            System.out.println();
        } else {
            System.out.println("O número " + number + " não está entre 5 e 15.");
            System.out.println();
        }
    }
}

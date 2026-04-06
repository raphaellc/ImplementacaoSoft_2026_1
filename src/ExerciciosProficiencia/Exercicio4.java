package ExerciciosProficiencia;

import java.util.Random;

public class Exercicio4 {
    public static void main(String[] args) {
        int[] vetor = new int[10];
        Random random = new Random();
        int soma = 0;
        String valoresGerados = "";

        IO.println("Valores gerados no vetor:");
        for (int i = 0; i < vetor.length; i++) {
            vetor[i] = random.nextInt(31) + 20;
            valoresGerados += vetor[i] + " ";
            soma += vetor[i];
        }

        IO.println(valoresGerados);
        IO.println("Resultado da soma de todos os 10 valores: " + soma);
    }
}
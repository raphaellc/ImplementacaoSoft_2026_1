package ExerciciosProficiencia;

import java.util.Arrays;

public class Exercicio5 {
    public static void main(String[] args) {
        int[] vetor = new int[10];

        IO.println("Digite 10 valores inteiros");
        for (int i = 0; i < vetor.length; i++) {
            IO.println(i);
            vetor[i] = Integer.parseInt(IO.readln());
        }

        IO.println("Valores invertido");
        String invertidos = "";
        for (int i = vetor.length - 1; i >= 0; i--) {
            invertidos += vetor[i] + " ";
        }
        IO.println(invertidos);

        int maior = vetor[0];
        int menor = vetor[0];
        for (int i = 1; i < vetor.length; i++) {
            if (vetor[i] > maior) maior = vetor[i];
            if (vetor[i] < menor) menor = vetor[i];
        }
        IO.println("Maior valor: " + maior);
        IO.println("Menor valor: " + menor);

        IO.println("Vetor antes da rotação: " + Arrays.toString(vetor));

        int temp = vetor[0];
        for (int i = 0; i < vetor.length - 1; i++) {
            vetor[i] = vetor[i + 1];
        }
        vetor[vetor.length - 1] = temp;

        IO.println("Vetor após rotação à esquerda: " + Arrays.toString(vetor));
    }
}
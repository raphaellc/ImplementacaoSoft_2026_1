package Aula02;
import java.util.Arrays;
import java.util.Random;

public class Exercicio5 {
    public static void main(String[] args) {
        int[] vetor = new int[10];
        Random random = new Random();

        int min = 1;
        int max = 20;
        
        for(int i = 0; i < vetor.length; i++){
            vetor[i] = random.nextInt(max - min + 1) + min;
        }

        System.out.println("Vetor original: " + Arrays.toString(vetor));
        System.out.println("Vetor invertido: ");

        for(int i = vetor.length - 1; i >= 0; i --){
            System.out.print(vetor[i] + " ");
        }
    
        int maior = vetor[0];
        int menor = vetor[0];

        for(int i = 1; i < vetor.length; i++){
            if (vetor[i] > maior) maior = vetor[i];
            if (vetor[i] < menor) menor = vetor[i];
        }

        System.out.println("\nMaior: " + maior + " - Menor: " + menor);    
    }
}

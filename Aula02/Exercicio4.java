package Aula02;
import java.util.Arrays;
import java.util.Random;

public class Exercicio4 {
    public static void main(String[] args) {
        int[] vetor = new int[10];
        Random random = new Random();
        int min = 20;
        int max = 50; 

        for(int i = 0; i < vetor.length; i++){
            vetor[i] = random.nextInt(max - min + 1) + min;
        }

        System.out.println("Números gerados: " + Arrays.toString(vetor));

        int soma = 0;
        for(int n : vetor){
            soma += n;
        }

        System.out.println("Soma total: " + soma);
    }
}

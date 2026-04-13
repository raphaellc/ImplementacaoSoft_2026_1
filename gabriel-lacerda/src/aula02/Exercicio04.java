package aula02;

import java.util.Random;

 public class Exercicio04 {
     public static void main(String[] args) {
         int[] vetor = new int[10];
         Random rand = new Random();
         int soma = 0;

         for (int i = 0; i < vetor.length; i++) {
             vetor[i] = 20 + rand.nextInt(31);
             soma += vetor[i];
         }

         System.out.println("Soma: " + soma);
     }
 }


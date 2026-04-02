 import java.util.Scanner;

 public class Exercicio03 {
     public static void main(String[] args) {
         Scanner sc = new Scanner(System.in);
         int n;

         while (true) {
             System.out.print("Digite um numero: ");
             n = sc.nextInt();

             if (n == 0) break;
             if (n >= 5 && n <= 15)
                 System.out.println(n);
         }

         sc.close();
     }
 }

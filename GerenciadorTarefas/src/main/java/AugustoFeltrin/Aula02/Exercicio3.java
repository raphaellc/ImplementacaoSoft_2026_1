package AugustoFeltrin.Aula02;

import java.util.Scanner;
import java.util.ArrayList;

public class Exercicio3 {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int numero;
        ArrayList<Integer> validos = new ArrayList<>();
        
        System.out.println("Digite números inteiros positivos (digite 0 para sair):");
        while(true){
            numero = scan.nextInt();           
            if(numero == 0){
                break;
            }

            if(numero >= 5 && numero <= 15){
                validos.add(numero);
            }
        }
        System.out.println("Números digitados que estão no intervalo [5-15]:");
        System.out.println(validos);
        scan.close();
    }
}

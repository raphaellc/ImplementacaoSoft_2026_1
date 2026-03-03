import java.util.Scanner;

public class Exercicio1{
    public static void main(String args[]){
        Scanner scan = new Scanner(System.in);

        System.out.println("Digite quantas linhas deseja preencher: ");
        int qtdLinhas = scan.nextInt();

        System.out.println("Digite quantas colunas deseja preencher: ");
        int qtdColunas = scan.nextInt();

        int [][] matriz = new int [qtdLinhas][qtdColunas];

        System.out.println("Agora, preenche os valres da matriz: ");
        for(int i = 0; i < qtdLinhas; i++){
            for (int j = 0; j < qtdColunas; j++){
                System.out.printf("Elemento [%d][%d]: ", i, j);
                matriz[i][j] = scan.nextInt();
            }
        }

        System.out.println("Sua matriz foi criada: ");
        for(int i = 0; i < qtdLinhas; i++){
            for (int j = 0; j < qtdColunas; j++){
                    System.out.println("[" + matriz[i][j] + "]");
            }
        }
    }
}
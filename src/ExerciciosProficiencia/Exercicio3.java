package ExerciciosProficiencia;

public class Exercicio3 {
    public static void main(String[] args) {
        int numero;

        IO.println("Digite números inteiros positivos (0 para sair):");

        while (true) {
            numero = Integer.parseInt(IO.readln());

            if (numero == 0) {
                IO.println("Saindo...");
                break;
            }

            if (numero > 0) {
                if (numero >= 5 && numero <= 15) {
                    IO.println(numero);
                }
            } else {
                IO.println("Apenas inteiros positivos são aceitos.");
            }
        }
    }
}
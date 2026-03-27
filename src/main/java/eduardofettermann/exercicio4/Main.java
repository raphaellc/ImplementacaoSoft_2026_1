package eduardofettermann.exercicio4;

public class Main {
    public static void main(String[] args) {
        System.out.println();
        System.out.println("Esse programa imprime a soma dos valores de um vetor de 10 elementos aleatórios entre 20 e 50.");
        int [] numbersToSum = new int [10];
        
        int minValue = 20;
        int maxValue = 50;

        int sum = 0;

        for (int i = 0; i < numbersToSum.length; i++) {
            int randomNumberBetweenMinAndMax = (int) (Math.random() * (maxValue - minValue + 1) + minValue);
            numbersToSum[i] = randomNumberBetweenMinAndMax;
            sum += randomNumberBetweenMinAndMax;
            System.out.println("O valor " + (i + 1) + " do vetor é: " + randomNumberBetweenMinAndMax);
            System.out.println("E o valor atual da soma é: " + sum);
            System.out.println();
        }

        System.out.println();
        System.out.println("A soma dos valores do vetor é: " + sum);
        System.out.println();
    }
}

import java.util.Random;

public class ArraySum {
	public static void main(String[] args) {
		int sum = 0;
		int[] numbers = new int[10];
		Random random = new Random();

		for (int i = 0; i < numbers.length; i++) {
			int number = random.nextInt(31) + 20;

			IO.println("Número gerado: " + number);

			numbers[i] = number;

			sum += number;
		}

		IO.println("Soma dos 10 números gerados: " + sum);
	}
}

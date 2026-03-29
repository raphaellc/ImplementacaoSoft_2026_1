import java.util.Random;

public class InvertedArray {
	static void generateAndPrintRandomNumbers() {
		Integer min = null;
		Integer max = null;
		int[] numbers = new int[10];
		Random random = new Random();

		for (int i = 0; i < numbers.length; i++) {
			int number = random.nextInt();

			if (min == null || number < min) {
				min = number;
			}

			if (max == null || number > max) {
				max = number;
			}

			numbers[i] = number;
		}

		for (int i = numbers.length - 1; i >= 0; i--) {
			IO.println("Número gerado: " + numbers[i] + " na posição " + i);
		}

		IO.println("Menor número gerado: " + min);
		IO.println("Maior número gerado: " + max);
	}

	static int[] rotateArray(int[] array) {
		int[] rotated = new int[array.length];

		for (int i = 0; i < array.length; i++) {
			if (i + 1 < array.length) {
				rotated[i] = array[i + 1];
			} else {
				rotated[i] = array[0];
			}
		}

		return rotated;
	}

	static void printArray(int[] array) {
		IO.println("Números:");

		for (int number : array) {
			IO.println(number);
		}
	}

	public static void main(String[] args) {
		generateAndPrintRandomNumbers();

		int[] numbers = { 1, 2, 3, 4, 5 };

		numbers = rotateArray(numbers);
		printArray(numbers);

		numbers = rotateArray(numbers);
		printArray(numbers);
	}
}

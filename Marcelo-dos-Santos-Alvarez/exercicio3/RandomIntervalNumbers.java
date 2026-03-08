import java.util.ArrayList;

public class RandomIntervalNumbers {
	static ArrayList<Integer> readNumbers() {
		ArrayList<Integer> numbers = new ArrayList<>();

		while (true) {
			try {
				int number = Integer.parseInt(
						IO.readln("Informe um número aleatório positivo, para sair digite 0, número informado: "));

				if (number == 0) {
					break;
				}

				numbers.add(number);
			} catch (NumberFormatException e) {
				IO.println("Informe um número válido!");
			}
		}

		return numbers;
	}

	static void printNumbers(ArrayList<Integer> numbers) {
		for (int number : numbers) {
			if (number > 4 && number < 16) {
				IO.println(number);
			}
		}
	}

	public static void main(String[] args) {
		ArrayList<Integer> numbers = readNumbers();

		IO.println("Números no intervalo (5-15):");

		printNumbers(numbers);
	}
}

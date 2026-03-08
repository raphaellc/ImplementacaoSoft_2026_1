import java.util.Random;

public class Board {
	static int readInt(String prompt) {
		while (true) {
			try {
				return Integer.parseInt(IO.readln(prompt));
			} catch (NumberFormatException e) {
				IO.println("Informe um número válido!");
			}
		}
	}

	static String readLetters(String prompt) {
		while (true) {
			String word = IO.readln(prompt);

			if (word.length() == 5 && word.matches("[a-zA-Z]+")) {
				return word;
			}

			IO.println("Informe exatamente 5 letras!");
		}
	}

	static void printBoard(char[][] board) {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] == '\0') {
					IO.print(". ");
				} else {
					IO.print(board[i][j] + " ");
				}
			}
			IO.println();
		}
	}

	public static void main(String[] args) {
		int rows = readInt("Informe um número de linhas: ");
		int cols = readInt("Informe um número de colunas: ");
		String letters = readLetters("Informe 5 letras aleatórias: ");

		char[][] board = new char[rows][cols];
		Random random = new Random();

		int limit = Math.min(letters.length(), rows * cols);

		for (int i = 0; i < limit; i++) {
			while (true) {
				int r = random.nextInt(rows);
				int c = random.nextInt(cols);

				if (board[r][c] == '\0') {
					board[r][c] = letters.charAt(i);
					break;
				}
			}
		}

		printBoard(board);
	}
}

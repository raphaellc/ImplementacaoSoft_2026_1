package eduardofettermann.exercicio1;

import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        char[][] matrix = askUserForMatrix(scanner);

        String selectedLetters = askUserForLetters(scanner);

        printNSpaces(2);

        generateMatrixWithRandomLetters(matrix, selectedLetters);

        printNSpaces(2);
        printMatrix(matrix);
        printNSpaces(2);
    }

    public static void printMatrix(char[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void printNSpaces(int n) {
        for (int i = 0; i < n; i++) {
            System.out.println();
        }
    }

    public static void generateMatrixWithRandomLetters(char[][] matrix, String selectedLetters) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = selectedLetters.charAt(new Random().nextInt(selectedLetters.length()));
            }
        }
    }

    public static String askUserForLetters(Scanner scanner) {
        System.out.println("Digite as 5 letras que você deseja espalhar pela matriz(ex.: 'ABCDE'): ");
        String letters = scanner.next();
        if (!validateLetters(letters)) {
            return askUserForLetters(scanner);
        }
        if (!validateNumberOfLetters(letters)) {
            return askUserForLetters(scanner);
        }
        return letters;
    }

    public static boolean validateLetters(String letters) {
        if (!letters.matches("^[a-zA-Z]+$")) {
            System.out.println("Você deve inserir apenas letras.");
            return false;
        }
        return true;
    }

    public static boolean validateNumberOfLetters(String letters) {
        if (letters.length() < 5 || letters.length() > 5) {
            System.out.println("Você deve inserir exatamente 5 letras.");
            return false;
        }
        return true;
    }

    public static char[][] askUserForMatrix(Scanner scanner) {
        System.out.println("Digite a quantidade de linhas da matriz: ");
        int rows = scanner.nextInt();
        System.out.println("Digite a quantidade de colunas da matriz: ");
        int columns = scanner.nextInt();
        
        if (!isValidMatrix(rows, columns)) {
            return askUserForMatrix(scanner);
        }

        return new char[rows][columns];
    }

    public static boolean isValidMatrix(int rows, int columns) {
        if (rows < 1 || columns < 1) {
            System.out.println("A quantidade de linhas e colunas devem ser maiores que 0.");
            return false;
        }
        return true;
    }
}

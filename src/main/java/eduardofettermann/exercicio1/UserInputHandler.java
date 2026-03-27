package eduardofettermann.exercicio1;

import java.util.Scanner;

public class UserInputHandler {

    private final MatrixValidator validator;

    public UserInputHandler(MatrixValidator validator) {
        this.validator = validator;
    }

    public char[][] askForMatrix(Scanner scanner) {
        System.out.println("Digite a quantidade de linhas da matriz: ");
        int rows = scanner.nextInt();
        System.out.println("Digite a quantidade de colunas da matriz: ");
        int columns = scanner.nextInt();

        if (!validator.isValidMatrix(rows, columns)) {
            return askForMatrix(scanner);
        }

        return new char[rows][columns];
    }

    public String askForLetters(Scanner scanner) {
        System.out.println("Digite as 5 letras que você deseja espalhar pela matriz(ex.: 'ABCDE'): ");
        String letters = scanner.next();
        if (!validator.validateLetters(letters)) {
            return askForLetters(scanner);
        }
        if (!validator.validateNumberOfLetters(letters)) {
            return askForLetters(scanner);
        }
        return letters;
    }
}

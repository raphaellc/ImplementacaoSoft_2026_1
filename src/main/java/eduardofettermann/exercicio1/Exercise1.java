package eduardofettermann.exercicio1;

import java.util.Scanner;

public class Exercise1 {

    private static final int EMPTY_LINES_AROUND_MATRIX = 2;

    public void run() {
        Scanner scanner = new Scanner(System.in);
        MatrixValidator validator = new MatrixValidator();
        UserInputHandler userInputHandler = new UserInputHandler(validator);
        MatrixGenerator matrixGenerator = new MatrixGenerator();
        MatrixPrinter matrixPrinter = new MatrixPrinter();

        char[][] matrix = userInputHandler.askForMatrix(scanner);
        String selectedLetters = userInputHandler.askForLetters(scanner);

        matrixPrinter.printEmptyLines(EMPTY_LINES_AROUND_MATRIX);
        matrixGenerator.fillWithRandomLetters(matrix, selectedLetters);
        matrixPrinter.printEmptyLines(EMPTY_LINES_AROUND_MATRIX);
        matrixPrinter.printMatrix(matrix);
        matrixPrinter.printEmptyLines(EMPTY_LINES_AROUND_MATRIX);
    }
}

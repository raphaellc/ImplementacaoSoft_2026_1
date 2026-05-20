package eduardofettermann.exercicio1;

import java.util.Random;

public class MatrixGenerator {

    public void fillWithRandomLetters(char[][] matrix, String selectedLetters) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = selectedLetters.charAt(new Random().nextInt(selectedLetters.length()));
            }
        }
    }
}

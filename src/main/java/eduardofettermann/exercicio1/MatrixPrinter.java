package eduardofettermann.exercicio1;

public class MatrixPrinter {

    public void printMatrix(char[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void printEmptyLines(int n) {
        for (int i = 0; i < n; i++) {
            System.out.println();
        }
    }
}

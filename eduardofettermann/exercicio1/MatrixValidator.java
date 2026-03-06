package eduardofettermann.exercicio1;

public class MatrixValidator {

    public boolean isValidMatrix(int rows, int columns) {
        if (rows < 1 || columns < 1) {
            System.out.println("A quantidade de linhas e colunas devem ser maiores que 0.");
            return false;
        }
        return true;
    }

    public boolean validateLetters(String letters) {
        if (!letters.matches("^[a-zA-Z]+$")) {
            System.out.println("Você deve inserir apenas letras.");
            return false;
        }
        return true;
    }

    public boolean validateNumberOfLetters(String letters) {
        if (letters.length() < 5 || letters.length() > 5) {
            System.out.println("Você deve inserir exatamente 5 letras.");
            return false;
        }
        return true;
    }
}

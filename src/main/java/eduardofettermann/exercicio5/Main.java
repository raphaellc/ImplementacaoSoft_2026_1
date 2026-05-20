package eduardofettermann.exercicio5;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int[] array = new int[10];
        fillArray(array);
        printInverseArray(array);
        printMaxAndMin(array);
        rotateLeft(array);
        rotateLeft(array);
    }

    private static void fillArray(int[] array) {
        System.out.println();
        System.out.println("Array preenchido:");
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt(10);
        }
        printArray(array);
    }

    private static void printInverseArray(int[] array) {
        System.out.println();
        System.out.println("Array invertido:");
        int[] inverseArray = array.clone();
        for (int i = array.length - 1; i >= 0; i--) {
            inverseArray[i] = array[array.length - 1 - i];
        }
        printArray(inverseArray);
    }

    private static void printMaxAndMin(int[] array) {
        int max = array[0];
        int min = array[0];
        for (int i = 0; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }

            if (array[i] < min) {
                min = array[i];
            }
        }
        System.out.println();
        System.out.println("Exibindo o maior e o menor valor do array:");
        System.out.println("Maior valor: " + max);
        System.out.println("Menor valor: " + min);
        System.out.println();
    }

    private static void rotateLeft(int[] array) {
        System.out.println("Array original:");
        printArray(array);
        System.out.println();
        int firstElement = array[0];
        for (int i = 0; i < array.length - 1; i++) {
            array[i] = array[i + 1];
        }
        array[array.length - 1] = firstElement;
        System.out.println("Array após a rotação à esquerda:");
        printArray(array);
        System.out.println();
    }

    private static void printArray(int[] array) {
        System.out.println(arrayToString(array));
    }

    private static String arrayToString(int[] array) {
        StringBuilder arrayString = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            arrayString.append(array[i]);
            if (i < array.length - 1) {
                arrayString.append(", ");
            }
        }
        arrayString.append("]");
        return arrayString.toString();
    }
}

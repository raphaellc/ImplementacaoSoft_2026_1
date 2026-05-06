import java.util.Arrays;
import java.util.Random;

public class exercicio5 {
    public static void main(String[] args) {
        int[] nums = new int[10];

        var random = new Random();

        for (int i = 0; i < nums.length; i++) {
            nums[i] = random.nextInt(10) + 1;
        }

        System.out.println("Array: "+Arrays.toString(nums));

        int maior = nums[0];
        int menor = nums[0];

        for (int i = 1; i < nums.length; i++) {
            if(nums[i] > maior)
                maior = nums[i];

            if(nums[i] < menor)
                menor = nums[i];
        }

        System.out.println("Maior: "+maior+"\nMenor: "+menor);

        int first = nums[0];
        var tempArray = new int[10];

        for (int i = 1; i < tempArray.length; i++) {
            tempArray[i-1] = nums[i];
        }

        tempArray[tempArray.length - 1] = first;

        System.out.println("Array rotacionado para esquerda: "+Arrays.toString(tempArray));
    }
}

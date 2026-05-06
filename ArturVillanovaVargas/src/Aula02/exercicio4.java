import java.util.Arrays;
import java.util.Random;

public class exercicio4 {
    public static void main(String[] args) {
        var nums = new int[10];

        var random = new Random();

        for (int i = 0; i < nums.length; i++) {
            nums[i] = random.nextInt(50) + 20;
        }

        System.out.println("Array original: " + Arrays.toString(nums));
        System.out.println("Soma de todos os números: " + Arrays.stream(nums).sum());
    }
}

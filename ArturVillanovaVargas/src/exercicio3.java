import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class exercicio3 {
    static void main(String[] args) {

        var nums = new ArrayList<Integer>();

        var scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Digite um número\nou (0) para parar:");
            int num = Integer.parseInt(scanner.nextLine());

            if(num == 0)
                break;

            nums.add(num);
        }

        System.out.println("Os números que você escolheu foi: \n"+nums);
        System.out.println("Os números entre 5 e 15 são: "+ Arrays.toString(nums.stream().filter(num -> (num >= 5 && num <= 15)).toArray()));
    }
}
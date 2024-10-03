import java.util.Scanner;

public class Test3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = scanner.nextInt();
        }
        scanner.close();

        int sumConnect = 0;
        for (int j : a) {
            sumConnect += j;
        }

        if (sumConnect % 2 == 0) {
            System.out.println(1);
        }else {
            System.out.println(0);
        }
    }
}



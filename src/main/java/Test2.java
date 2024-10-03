import java.util.*;

public class Test2 {
    public void test2() {
        Scanner sc = new Scanner(System.in);
        char[] MTS = {'M', 'T', 'S'};
        char[] mtsString = sc.nextLine().toUpperCase().toCharArray();
        List<Character> mtsStringArr = new LinkedList<>();
        for (char c : mtsString) {
            mtsStringArr.add(c);
        }

        Iterator<Character> it = mtsStringArr.iterator();
        while (it.hasNext()) {
            Character c = it.next();
            if (c == MTS[0] || c == MTS[1] || c == MTS[2]) {
                continue;
            } else {
                it.remove();
            }
        }

        int index = 0;
        for (int i = 0; i < mtsStringArr.size(); i++) {
            if (Objects.equals(mtsStringArr.get(i), MTS[index])) {
                index++;
                if (index == MTS.length) {
                    break;
                }
            }
        }

        if (index == MTS.length) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }
    }

    public int test2(String input) {
        char[] MTS = {'M', 'T', 'S'};
        char[] mtsString = input.toUpperCase().toCharArray();
        List<Character> mtsStringArr = new LinkedList<>();
        for (char c : mtsString) {
            mtsStringArr.add(c);
        }
        Iterator<Character> it = mtsStringArr.iterator();
        while (it.hasNext()) {
            Character c = it.next();
            if (c == MTS[0] || c == MTS[1] || c == MTS[2]) {
                continue;
            } else {
                it.remove();
            }
        }

        int index = 0;
        for (int i = 0; i < mtsStringArr.size(); i++) {
            if (Objects.equals(mtsStringArr.get(i), MTS[index])) {
                index++;
                if (index == MTS.length) {
                    break;
                }
            }
        }

        if (index == MTS.length) {
            return 1;
        } else {
            return 0;
        }
    }
}

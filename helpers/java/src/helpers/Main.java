package helpers;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Helper help = new Helper();
        String name = "text.txt";
//        help.writeFile(name, 0, 10, 50);
        System.out.println(help.floatFilesEqual(name, "text2.txt"));
        Float[] a = help.readFileFloat(name);
        System.out.println(Arrays.toString(a));
    }
}

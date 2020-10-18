package helpers;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Helper help = new Helper();
        String name = "text.txt";
        help.writeFile(name, 0, 10, 500);
//        System.out.println(help.floatFilesEqual(name, "text2.txt"));
    }
}

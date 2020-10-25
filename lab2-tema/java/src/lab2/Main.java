package lab2;
public class Main {

    public static void main(String[] args) {
        Test tester = new Test();
        try {
//            tester.createFiles();
            tester.test1(4);
            tester.allTest2();
            tester.allTest3();
            tester.allTest4();
        } catch (Exception e) {
            System.out.println("Tests failed!");
            System.out.println(e.getMessage());
        }
    }
}

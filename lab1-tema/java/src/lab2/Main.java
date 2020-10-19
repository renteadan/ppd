package lab2;
public class Main {

    public static void main(String[] args) throws InterruptedException {
        Test tester = new Test();
        tester.newFile = true;
        tester.test1(4);
        tester.newFile = true;
        tester.allTest2();
        tester.newFile = true;
        tester.allTest3();
        tester.newFile = true;
        tester.allTest4();
    }
}

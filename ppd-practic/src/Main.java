
import helpers.Helper;

import java.util.concurrent.ConcurrentHashMap;

public class Main {

    static ConcurrentHashMap<String, Integer> parallelResult;
    private static int nrFiles = 1;
    public static void main(String[] args) throws Exception {

//        int maxGrade = 10000, maxMonomials = 500;
//        Main.rewrite(nrFiles, maxGrade, maxMonomials);
        Main.run();
    }

    public static void rewrite(int nrPolynomials, int maxGrade, int maxMonomials) {
        Helper helper = new Helper();
        for(int i = 0; i< nrPolynomials; i++) {
            helper.writePolynomial("polynoms/polinom"+i+".txt", maxGrade, maxMonomials);
        }
    }

    public static long runTimesParallel(int times) throws InterruptedException {
        long total = 0;
        for(int i=0;i<times;i++) {
            Exchange exchange = new Exchange(2);
            int consumerThreads = 4;
            int publisherThreads = 4;
            ThreadRunner threadRunner = new ThreadRunner(consumerThreads, publisherThreads, nrFiles, exchange);
            long start = System.currentTimeMillis();
            parallelResult = threadRunner.runPool();
            long duration = System.currentTimeMillis() - start;
            total+=duration;
        }
        return total / times;
    }

    public static void run() throws Exception {
        long duration_p = Main.runTimesParallel(1);
        System.out.println("Parallel time was " + duration_p + " milliseconds");
    }
}

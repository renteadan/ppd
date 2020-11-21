package lab4;

import helpers.Helper;

import java.util.Vector;

public class Main {

    static Vector<Monomial> sequentialResult, parallelResult;
    public static void main(String[] args) throws Exception {

        IQueue<Monomial> queue = new BlockingQueueImpl<>();
//        IQueue<Monomial> queue = new SyncQueue<>();
        int maxGrade = 10000, maxMonomials = 500;
//        Main.rewrite(nrPolys, maxGrade, maxMonomials);
        Main.run(queue);
    }

    public static void rewrite(int nrPolynomials, int maxGrade, int maxMonomials) {
        Helper helper = new Helper();
        for(int i = 0; i< nrPolynomials; i++) {
            helper.writePolynomial("polynoms/polinom"+i+".txt", maxGrade, maxMonomials);
        }
    }

    public static void run(IQueue<Monomial> queue) throws Exception {
        int nrPolys = 1000, consumerThreads = 8, publisherThreads = 1;
        ThreadRunner threadRunner = new ThreadRunner(consumerThreads, publisherThreads, nrPolys, queue);
        long start_t = System.currentTimeMillis();
        parallelResult = threadRunner.runPool();
        long duration_p = System.currentTimeMillis() - start_t;

        System.out.println("Parallel: " + duration_p);

        SequentialRunner sequentialRunner = new SequentialRunner(nrPolys);
        long start_s = System.currentTimeMillis();
        sequentialResult = sequentialRunner.run();
        long duration_s = System.currentTimeMillis() - start_s;

        System.out.println("Sequential: " + duration_s);

        if(parallelResult.size() != sequentialResult.size()) {
            System.out.println(parallelResult.size());
            System.out.println(sequentialResult.size());
            throw new Exception("Results sizes not equal!");
        }
        for(int i=0; i<sequentialResult.size(); i++) {
            if(!sequentialResult.get(i).equals(parallelResult.get(i))) {
                System.out.println(parallelResult.get(i));
                System.out.println(sequentialResult.get(i));
                throw new Exception("Results not equal!");
            }
        }

        if(duration_s > duration_p) {
            System.out.println("Parallel was faster with " + (duration_s - duration_p) + " milliseconds");
        } else {
            System.out.println("Sequential was faster with " + (duration_p - duration_s) + " milliseconds");
        }
    }
}

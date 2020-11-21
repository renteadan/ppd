package lab4;

import helpers.Helper;

import java.util.Vector;

public class Main {
    public static void main(String[] args) throws Exception {
        int nrPolys = 1000, maxGrade = 10000, maxMonomials = 500, consumerThreads = 8, publisherThreads = 8;
//        Main.rewrite(nrPolys, maxGrade, maxMonomials);

        long start_t = System.currentTimeMillis();
        ThreadRunner threadRunner = new ThreadRunner(consumerThreads, publisherThreads, nrPolys);
        Vector<Monomial> parallelResult = threadRunner.run();
        long duration_p = System.currentTimeMillis() - start_t;

        System.out.println("Parallel: " + duration_p);

        long start_s = System.currentTimeMillis();
        SequentialRunner sequentialRunner = new SequentialRunner(nrPolys);
        Vector<Monomial> sequentialResult = sequentialRunner.run();
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

    public static void rewrite(int nrPolynomials, int maxGrade, int maxMonomials) {
        Helper helper = new Helper();
        for(int i = 0; i< nrPolynomials; i++) {
            helper.writePolynomial("polynoms/polinom"+i+".txt", maxGrade, maxMonomials);
        }
    }
}

package lab4;

import helpers.Helper;

import java.util.Vector;

public class Main {

	static Vector<Monomial> sequentialResult, parallelResult;
	private static int nrPolys = 10000;
	public static void main(String[] args) throws Exception {

		int maxGrade = 10000, maxMonomials = 500;
//        Main.rewrite(nrPolys, maxGrade, maxMonomials);
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
			ThreadRunner threadRunner = new ThreadRunner(consumerThreads, publisherThreads, nrPolys, exchange);
			long start = System.currentTimeMillis();
			parallelResult = threadRunner.runPool();
			long duration = System.currentTimeMillis() - start;
			total+=duration;
		}
		return total / times;
	}

	public static long runTimesSeq(int times) {
		long total = 0;
		for(int i=0;i<times;i++) {
			SequentialRunner sequentialRunner = new SequentialRunner(nrPolys);
			long start = System.currentTimeMillis();
			sequentialResult = sequentialRunner.run();
			long duration = System.currentTimeMillis() - start;
			total+=duration;
		}
		return total / times;
	}

	public static void run() throws Exception {
		long duration_p = Main.runTimesParallel(5);
		System.out.println("Parallel: " + duration_p);

		long duration_s = Main.runTimesSeq(1);

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

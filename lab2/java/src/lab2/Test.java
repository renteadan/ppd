package lab2;

import helpers.Helper;

import java.util.Arrays;

public class Test {
		Helper help = new Helper();
		Matrix mat, filter;
		boolean newFile;
		public void createFile(int size) {
				help.writeFile("date.txt", 1, 1000, size);
				newFile = false;
		}

		private void createFilter(int i, int j) {
				filter = new Matrix(i, j);
				Double[] fillArr = new Double[i*j];
				Arrays.fill(fillArr, 0.5);
				filter.setMatrixFromArray(fillArr);
		}

		private void createMatrix(int i, int j, String file) {
				mat = new Matrix(i, j);
				Double[] a = help.readFileDouble("date.txt");
				mat.setMatrixFromArray(a);
		}

		public void allTest2() {
				newFile = true;
				test2(2);
				test2(4);
				test2(8);
				test2(16);
		}

		public void allTest3() {
				newFile = true;
				test3(2);
				test3(4);
				test3(8);
				test3(16);
		}

		public void allTest4() {
				newFile = true;
				test4(2);
				test4(4);
				test4(8);
				test4(16);
		}

		private long filterLinearDuration() {
				long start_t = System.nanoTime();
				mat.filterMatrix(filter);
				return System.nanoTime() - start_t;
		}

		private long filterParallelDuration(int threads) {
				long start_t = System.nanoTime();
				mat.filterMatrixParallel(filter, threads);
				return System.nanoTime() - start_t;
		}

		private long averageParallel(int threads, int n) {
				long sum = 0;
				for(int i=0;i<n;i++) {
						long duration = filterParallelDuration(threads);
						sum+=duration;
				}
				return sum/n;
		}

		private long averageLinear(int n) {
				long sum = 0;
				for(int i=0;i<n;i++) {
						long duration = filterLinearDuration();
						sum+=duration;
				}
				return sum/n;
		}

		public void test1(int threads) {
				System.out.println("Test 1; Threads=" + threads);
				int n = 10, m = 10;
				createFile(n*m);
				createFilter(3, 3);
				createMatrix(n, m, "date.txt");
				long lin = averageLinear(5);
				long par = averageParallel(4, 5);
				printComparisons(lin, par);
		}

		public void test2(int threads) {
				System.out.println("Test 2; Threads=" + threads);
				int n = 1000, m = 1000;
				if(newFile)
					createFile(n*m);
				createFilter(5, 5);
				createMatrix(n, m, "date.txt");
				long lin = averageLinear(5);
				long par = averageParallel(4, 5);
				printComparisons(lin, par);
		}

		public void test3(int threads) {
				System.out.println("Test 3; Threads=" + threads);
				int n = 10, m = 10000;
				if(newFile)
						createFile(n*m);
				createFilter(5, 5);
				createMatrix(n, m, "date.txt");
				long lin = averageLinear(5);
				long par = averageParallel(4, 5);
				printComparisons(lin, par);
		}

		public void test4(int threads) {
				System.out.println("Test 4; Threads=" + threads);
				int n = 10000, m = 10;
				if(newFile)
						createFile(n*m);
				createFilter(5, 5);
				createMatrix(n, m, "date.txt");
				long lin = averageLinear(5);
				long par = averageParallel(4, 5);
				printComparisons(lin, par);
		}

		private void printComparisons(long linear, long parallel) {
				System.out.println("Linear=" + linear);
				System.out.println("Parallel="+ parallel);
				if(linear < parallel)
						System.out.println("Linear is faster");
				else
						System.out.println("Parallel is faster");
		}
}

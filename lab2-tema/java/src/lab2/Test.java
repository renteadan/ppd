package lab2;

import helpers.Helper;

import java.util.Arrays;

public class Test {
		Helper help = new Helper();
		Matrix mat, filter, resultLinear, resultParallel;
		boolean newFile;
		public void createFile(int size, String name) {
				help.writeFile(name, 1, 1000, size);
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
				Double[] a = help.readFileDouble(file);
				mat.setMatrixFromArray(a);
		}

		public void allTest2() throws Exception {
				newFile = true;
				test2(2);
				test2(4);
				test2(8);
				test2(16);
		}

		public void allTest3() throws Exception {
				newFile = true;
				test3(2);
				test3(4);
				test3(8);
				test3(16);
		}

		public void allTest4() throws Exception {
				newFile = true;
				test4(2);
				test4(4);
				test4(8);
				test4(16);
		}

		public void createFiles() {
				createFile(100, "test1.txt");
				createFile(1000000, "test2.txt");
				createFile(100000, "test3.txt");
				createFile(100000, "test4.txt");

		}

		private long filterLinearDuration() {
				long start_t = System.nanoTime();
				resultLinear = mat.filterMatrix(filter);
				return System.nanoTime() - start_t;
		}

		private long filterParallelDuration(int threads) {
				long start_t = System.nanoTime();
				resultParallel.filterMatrixParallel(filter, threads);
				return System.nanoTime() - start_t;
		}

		private long averageParallel(int threads, int n) {
				long sum = 0;
				for(int i=0;i<n;i++) {
						resultParallel = mat.copy();
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

		public void test1(int threads) throws Exception {
				newFile = true;
				System.out.println("Test 1; Threads=" + threads);
				int n = 10, m = 10;
				calculateTimes(threads, n ,m, "test1.txt");
		}

		public void test2(int threads) throws Exception {
				System.out.println("Test 2; Threads=" + threads);
				int n = 1000, m = 1000;
				calculateTimes(threads, n, m, "test2.txt");
		}

		public void test3(int threads) throws Exception {
				System.out.println("Test 3; Threads=" + threads);
				int n = 10, m = 10000;
				calculateTimes(threads, n, m, "test3.txt");
		}

		public void test4(int threads) throws Exception {
				System.out.println("Test 4; Threads=" + threads);
				int n = 10000, m = 10;
				calculateTimes(threads, n, m, "test4.txt");
		}

		private void calculateTimes(int threads, int n, int m, String fileName) throws Exception {
				createFilter(5, 5);
				createMatrix(n, m, fileName);
				long lin = averageLinear(5);
				long par = averageParallel(threads, 5);
				if(!resultLinear.isEqual(resultParallel))
						throw new Exception("Results are not equal!");
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

package lab2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class TAdd  extends Thread{
		Matrix mat1, res, filter;
		int start,stop;
		Map<String, Double> cache = new HashMap<>();
		CyclicBarrier barrier;
		public TAdd(Matrix mat1, Matrix res, Matrix filter, int start, int stop, CyclicBarrier barrier) {
				this.mat1 = mat1;
				this.res = res;
				this.filter = filter;
				this.start = start;
				this.stop = stop;
				this.barrier = barrier;
		}

		private void transformMatrix() {
				for(int a=start; a<stop;a++) {
						int i = a / res.cols;
						int j = a % res.cols;
						res.storage[i][j] = applyFilter(i ,j);
				}
		}

		public void run() {
				for(int a=start; a<stop;a++) {
						int i = a / res.cols;
						int j = a % res.cols;
						readMatrix(i ,j);
				}
				try {
						barrier.await();
						transformMatrix();
				} catch (InterruptedException | BrokenBarrierException e) {
						e.printStackTrace();
				}
		}

		private double applyFilter(int i, int j) {
				int n = filter.lines;
				int m = filter.cols;
				int startY = Math.max(0, i - n/2);
				int stopY = Math.min(mat1.lines-1, i + n/2);
				int startX = Math.max(0, j - m/2);
				int stopX = Math.min(mat1.cols - 1, j+ m/2);
				double aux = 0;
				int a=0;
				for(int k=startY;k<=stopY; k++) {
						int b=0;
						for(int t=startX;t<=stopX;t++) {
								double fil = filter.getCell(a, b);
								String id = this.getIdentifier(k, t);
								double current = cache.get(id);

								aux = aux + fil * current;
								b++;
						}
						a++;
				}
				return aux;
		}

		private String getIdentifier(int i, int  j) {
				return "l" + i + "c" + j;
		}

		private void readMatrix(int i, int j) {
				int n = filter.lines;
				int m = filter.cols;
				int startY = Math.max(0, i - n/2);
				int stopY = Math.min(mat1.lines-1, i + n/2);
				int startX = Math.max(0, j - m/2);
				int stopX = Math.min(mat1.cols - 1, j+ m/2);
				for(int k=startY;k<=stopY; k++) {
						for(int t=startX;t<=stopX;t++) {
								double current = mat1.getCell(k, t);
								String id = this.getIdentifier(k, t);
								cache.putIfAbsent(id, current);
						}
				}
		}
}

package lab2;

import java.util.Vector;
import java.util.concurrent.CyclicBarrier;

public class Matrix {
		Double[][] storage;
		int lines, cols;
		public Matrix(int lines, int cols) {
				this.lines = lines;
				this.cols = cols;
				this.storage = new Double[lines][cols];
		}

		public void setMatrix(Double[][] matrice) {
				this.storage = matrice;
		}

		public void setMatrixFromArray(Double[] arr) {
				for(int i = 0; i< lines; i++) {
						if (cols >= 0) System.arraycopy(arr, i * cols, storage[i], 0, cols);
				}
		}

		public void printMatrix() {
				for(int i = 0; i< lines; i++) {
						for(int j=0;j<cols;j++) {
								System.out.print(storage[i][j]);
								System.out.print(" ");
						}
						System.out.print("\n");
				}
		}

		public double getCell(int i, int j) {
				if(i<0 || i>= lines)
						return 0;
				if(j<0 || j>=cols)
						return 0;
				return storage[i][j];
		}

		public double applyFilter(Matrix filter, int i, int j) {
				int n = filter.lines;
				int m = filter.cols;
				int startY = Math.max(0, i - n/2);
				int stopY = Math.min(this.lines-1, i + n/2);
				int startX = Math.max(0, j - m/2);
				int stopX = Math.min(this.cols - 1, j+ m/2);
				double aux = 0;
				int a=0;
				for(int k=startY;k<=stopY; k++) {
						int b=0;
						for(int t=startX;t<=stopX;t++) {
								double fil = filter.getCell(a, b);
								double current = this.getCell(k, t);
								aux = aux + fil * current;
								b++;
						}
						a++;
				}
				return aux;
		}

		public Matrix filterMatrix(Matrix filter) {
				Matrix aux = new Matrix(lines, cols);
				for(int i=0;i<lines;i++) {
						for(int j=0;j<cols;j++) {
								aux.storage[i][j] = this.applyFilter(filter,i, j);
						}
				}
				return aux;
		}

		public void filterMatrixParallel(Matrix filter, int nrThreads) {
				Vector<TAdd> threads = new Vector<>();
				int m = this.cols * this.lines;
				int chunk_size = m/nrThreads;
				int reminder = m%nrThreads;
				int start=0, end;
				CyclicBarrier barrier = new CyclicBarrier(nrThreads);
				for(int i=0;i<nrThreads;i++) {
						end = start+chunk_size;
						if (reminder > 0) {
								end+=1;
								reminder-=1;
						}
						threads.add(new TAdd(this, this ,filter, start, end, barrier));
						threads.get(i).start();
						start = end;
				}
				for(TAdd tr: threads){
						try {
								tr.join();
						} catch (InterruptedException ex) {
								System.out.println(ex.getMessage());
						}
				}
		}

		public boolean isEqual(Matrix mat2) {
				if(this.lines != mat2.lines || this.cols != mat2.cols)
						return false;
				for(int i=0;i<lines;i++) {
						for(int j=0;j<cols;j++) {
								if(storage[i][j].compareTo(mat2.getCell(i, j)) != 0)
										return false;
						}
				}
				return true;
		}

		private Double[][] deepCopyMatrix(Double[][] input) {
				if (input == null)
						return null;
				Double[][] result = new Double[input.length][];
				for (int i = 0; i < input.length; i++) {
						result[i] = input[i].clone();
				}
				return result;
		}

		public Matrix copy() {
				Matrix clone = new Matrix(lines, cols);
				clone.storage = deepCopyMatrix(this.storage);
				return clone;
		}
}

package lab2;

public class TAdd  extends Thread{
		Matrix mat1, res, filter;
		int start,stop;
		public TAdd(Matrix mat1, Matrix res, Matrix filter, int start, int stop) {
				this.mat1 = mat1;
				this.res = res;
				this.filter = filter;
				this.start = start;
				this.stop = stop;
		}

		public void run() {
				for(int a=start; a<stop;a++) {
						int i = a / res.cols;
						int j = a % res.cols;
						res.matrice[i][j] = applyFilter(filter,i ,j);
				}
		}

		public double applyFilter(Matrix filter, int i, int j) {
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
								double current = mat1.getCell(k, t);
								aux = aux + fil * current;
								b++;
						}
						a++;
				}
				return aux;
		}
}

import java.util.Vector;
import java.util.Random;
public class Main {
		public static void main(String[] args) throws InterruptedException {
				int m = 100000000, n=5;
				int[] a = new int[m];
				int[] b = new int[m];
				int[] c = new int[m];
				int[] d = new int[m];
				long start_t = System.currentTimeMillis();
				Vector<T1> threads = new Vector<>();
				Random r = new Random();
				for(int i=0;i<m;i++) {
						a[i] = r.nextInt(n);
						b[i] = r.nextInt(n);
				}
//				System.out.println(Arrays.toString(a));
//				System.out.println(Arrays.toString(b));
				int p=13;
				int chunk_size = m/p;
				int reminder = m%p;
				int start=0, end;
				for(int i=0;i<p;i++) {
						end = start+chunk_size;
						if (reminder > 0) {
								end+=1;
								reminder-=1;
						}
						threads.add(new T1(start ,end, a, b ,c));
						threads.get(i).start();
						start = end;
				}
				for(T1 tr: threads){
						tr.join();
				}
				System.out.println("paralel " + (System.currentTimeMillis() - start_t));
				start_t = System.currentTimeMillis();
				for(int i=0;i<m;i++) {
						d[i] = Main.apply(a[i], b[i]);
				}
				System.out.println("secvential " + (System.currentTimeMillis() - start_t));
//				System.out.println(Arrays.toString(c));
//				System.out.println(Arrays.toString(d));
		}

		static int apply(int a, int b) {
				return (int) Math.sqrt(Math.pow((a+b), 5));
		}

		public static class T1 extends Thread {
				private int start, end;
				int[] a,b,c;
				public T1(int start, int end, int[] a,int[] b,int[] c) {
						this.start = start;
						this.end = end;
						this.a = a;
						this.b = b;
						this.c = c;
				}
				public void run() {
//						System.out.println(currentThread().getName()+ " " + start + " " + end);
						for(int i=start;i<end;i++) {
								c[i] =  Main.apply(a[i], b[i]);
						}
				}
		}
}


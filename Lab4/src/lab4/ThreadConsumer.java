package lab4;

public class ThreadConsumer extends Thread {
		private Polynomial polynomial;
		private SyncQueue<Monomial> queue;

		private void addToPolynomial(Monomial m) {
				polynomial.addMonomial(m);
		}

		public ThreadConsumer(Polynomial polynomial, SyncQueue<Monomial> syncQueue) {
				this.polynomial = polynomial;
				this.queue = syncQueue;
		}

		public void run() {
				Monomial mon;
				while(queue.active || queue.elems > 0) {
						mon = queue.removeElement();
						if(mon != null)
								addToPolynomial(mon);
				}
		}
}

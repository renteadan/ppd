package lab4;

public class ThreadConsumer extends Thread {
		private Polynomial polynomial;
		private IQueue<Monomial> queue;
		public int processed = 0;

		private void addToPolynomial(Monomial m) {
				polynomial.addMonomial(m);
		}

		public ThreadConsumer(Polynomial polynomial, IQueue<Monomial> syncQueue) {
				this.polynomial = polynomial;
				this.queue = syncQueue;
		}

		public void run() {
				Monomial mon;
				while(queue.getActive() || queue.getElems() > 0) {
						try {
								mon = queue.removeElement();
								addToPolynomial(mon);
								processed++;
						} catch (InterruptedException e) {
								e.printStackTrace();
						}
				}
		}
}

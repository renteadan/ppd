package lab4;

import java.util.concurrent.LinkedBlockingQueue;

public class ThreadConsumer extends Thread {
		private Polynomial polynomial;
		private LinkedBlockingQueue<Monomial> queue = new LinkedBlockingQueue<>();
		public int processed = 0;
		private boolean active = true;

		private void addToPolynomial(Monomial m) {
				polynomial.addMonomial(m);
		}

		public ThreadConsumer(Polynomial polynomial) {
				this.polynomial = polynomial;
		}

		public void run() {
				Monomial mon;
				while(active || queue.size() > 0) {
							mon = queue.poll();
							if(mon == null)
								continue;;
							addToPolynomial(mon);
							processed++;
				}
		}

		public LinkedBlockingQueue<Monomial> getQueue() {
			return queue;
		}

		public void setInactive() {
			active = false;
		}
}

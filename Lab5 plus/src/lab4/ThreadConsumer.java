package lab4;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadConsumer extends Thread {
		private LinkedBlockingQueue<Monomial> queue = new LinkedBlockingQueue<>();
		public int processed = 0;
		private boolean active = true;
		private HashMap<Integer, Monomial> storage = new HashMap<>();
		private final Polynomial polynomial;

		private void addToPolynomial(Monomial m) {
			Monomial mon = storage.get(m.grade);
			if(mon == null) {
				storage.put(m.grade, m);
				return;
			}
			mon.coefficient += m.coefficient;
		}

		public ThreadConsumer(Polynomial polynomial) {
			this.polynomial = polynomial;
		}

		public void run() {
			while(active || queue.size() > 0) {
				Monomial mon = queue.poll();
				if(mon == null)
					continue;
				addToPolynomial(mon);
				processed++;
			}
			polynomial.addToStorage(storage);
			polynomial.increaseCount(processed);
		}

		public LinkedBlockingQueue<Monomial> getQueue() {
			return queue;
		}

		public void setInactive() {
			active = false;
		}
}

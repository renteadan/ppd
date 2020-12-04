package lab4;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Exchange {

	private LinkedBlockingQueue<ThreadConsumer> workers = new LinkedBlockingQueue<>();
	private ConcurrentLinkedQueue<Monomial> buffer = new ConcurrentLinkedQueue<>();
	private int processed = 0;
	private boolean active = true;
	private ExchangeRunner runner;
	public void publishMessage(Monomial message) {
		buffer.add(message);
	}

	Exchange() {
		run();
	}

	class ExchangeRunner extends Thread {
		public void run() {
			while(active || (buffer.size() > 0)) {
				try {
					ThreadConsumer consumer = workers.take();
					LinkedBlockingQueue<Monomial> queue = consumer.getQueue();
					Monomial message = buffer.poll();
					if(message != null) {
						queue.offer(message);
						processed++;
					}
					workers.put(consumer);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Finish distribute");
		}
	}

	public void run() {
		runner = new ExchangeRunner();
		runner.start();
	}

	public void subscribe(ThreadConsumer consumer) {
		workers.add(consumer);
	}

	public void unsubscribe(ThreadConsumer consumer) {
		workers.remove(consumer);
	}

	public void setInactive() {
		active = false;
		try {
			runner.join();
			for(ThreadConsumer c: workers) {
				c.setInactive();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

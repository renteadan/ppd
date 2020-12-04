package lab4;
import java.util.concurrent.LinkedBlockingQueue;

public class Exchange {

	private LinkedBlockingQueue<ThreadConsumer> workers = new LinkedBlockingQueue<>();
	private LinkedBlockingQueue<Monomial> buffer = new LinkedBlockingQueue<>();
	private boolean active = true;
	private ExchangeRunner runner;
	public void publishMessage(Monomial message) {
		buffer.offer(message);
	}

	Exchange() {
		run();
	}

	class ExchangeRunner extends Thread {
		public synchronized void run() {
			while(active || (buffer.size() > 0)) {
				try {
					ThreadConsumer consumer = workers.take();
					LinkedBlockingQueue<Monomial> queue = consumer.getQueue();
					Monomial message = buffer.poll();
					if(message != null)
						queue.offer(message);
					workers.put(consumer);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Finish consuming!");
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
		for(ThreadConsumer c: workers) {
			c.setInactive();
		}
		try {
			runner.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

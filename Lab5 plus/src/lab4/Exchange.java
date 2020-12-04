package lab4;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Exchange {

	private LinkedBlockingQueue<ThreadConsumer> workers = new LinkedBlockingQueue<>();
	private LinkedBlockingQueue<Monomial> buffer = new LinkedBlockingQueue<>();
	private int processed = 0;
	private AtomicInteger added = new AtomicInteger();
	private boolean active = true;
	private int distributers;
	private Vector<ExchangeRunner> runners;
	public void publishMessage(Monomial message) {
		buffer.offer(message);
		added.incrementAndGet();
	}

	Exchange(int distributers) {
		this.distributers = distributers;
		run();
	}

	class ExchangeRunner extends Thread {
		public void run() {
			while(active || (buffer.size() > 0)) {
				try {
					Monomial message = buffer.poll(1, TimeUnit.MICROSECONDS);
					if(message == null) {
						continue;
					}
					ThreadConsumer consumer = workers.take();
					LinkedBlockingQueue<Monomial> queue = consumer.getQueue();
					queue.offer(message);
					processed++;
					workers.put(consumer);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Finish distribute");
		}
	}

	public void run() {
		runners = new Vector<>();
		for(int i=0;i<distributers;i++) {
			ExchangeRunner runner = new ExchangeRunner();
			runner.start();
			runners.add(runner);
		}
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
			for(ExchangeRunner r: runners) {
				r.join();
			}
			for(ThreadConsumer c: workers) {
				c.setInactive();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}



import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadConsumer extends Thread {
	private LinkedBlockingQueue<DTO> queue = new LinkedBlockingQueue<>();
	public int processed = 0;
	private boolean active = true;
	private ConcurrentHashMap<String, Integer> storage;

	private void consume(DTO m) {
		storage.computeIfPresent(m.key, (key, val) -> val + m.value);
		storage.putIfAbsent(m.key, m.value);
	}

	public ThreadConsumer(ConcurrentHashMap<String, Integer> storage) {
		this.storage = storage;
	}

	public void run() {
		while(active || queue.size() > 0) {
			DTO mon = queue.poll();
			if(mon == null)
				continue;
			consume(mon);
			processed++;
		}
	}

	public LinkedBlockingQueue<DTO> getQueue() {
		return queue;
	}

	public void setInactive() {
		active = false;
	}
}

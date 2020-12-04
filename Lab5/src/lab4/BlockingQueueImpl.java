package lab4;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueImpl<T extends Comparable<T>> implements IQueue<T> {
		private BlockingQueue<T> queue = new LinkedBlockingQueue<>();
		public Boolean active = true;
		@Override
		public void addElement(T data) throws InterruptedException {
				queue.put(data);
		}

		@Override
		public T removeElement() throws InterruptedException {
				return queue.take();
		}

		@Override
		public int getElems() {
				return queue.size();
		}

		@Override
		public Boolean getActive() {
				return active;
		}

		@Override
		public void setActive(Boolean active) {
				this.active = active;
		}
}

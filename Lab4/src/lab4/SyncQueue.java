package lab4;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SyncQueue<T extends Comparable<T>> implements IQueue<T> {
		private final QueueLinkedList<T> list = new QueueLinkedList<>();
		private boolean active = true;
		private AtomicInteger counter = new AtomicInteger();

		private final ReentrantLock addLock = new ReentrantLock();
		private final Condition notEmpty = addLock.newCondition();
		private final ReentrantLock removeLock = new ReentrantLock();

		private void signal() {
				ReentrantLock takeLock = this.addLock;
				takeLock.lock();
				notEmpty.signal();
				takeLock.unlock();
		}

		public SyncQueue(){}

		public void addElement(T data) {
				int c;
				removeLock.lock();
				list.insert(data);
				c = counter.getAndIncrement();
				removeLock.unlock();
				if(c == 0)
					signal();
		}

		public T removeElement() throws InterruptedException {
				T data;
				int c;
				addLock.lock();
				while (counter.get() == 0) {
						notEmpty.await();
				}
				data = list.remove();
				c = counter.getAndDecrement();
				if (c > 0)
						notEmpty.signal();
				addLock.unlock();
				return data;
		}

		@Override
		public int getElems() {
				return counter.get();
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

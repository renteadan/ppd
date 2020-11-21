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

		/**
		 * Signals a waiting take. Called only from put/offer (which do not
		 * otherwise ordinarily lock takeLock.)
		 */
		private void signalNotEmpty() {
				final ReentrantLock takeLock = this.addLock;
				takeLock.lock();
				notEmpty.signal();
				takeLock.unlock();
		}

		public SyncQueue(){}

		public void addElement(T data) throws InterruptedException {
				final int c;
				removeLock.lockInterruptibly();
				list.insert(data);
				c = counter.getAndIncrement();
				removeLock.unlock();
				if(c == 0)
					signalNotEmpty();
		}

		public T removeElement() throws InterruptedException {
				final T data;
				final int c;
				addLock.lockInterruptibly();
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

package lab4;

import java.util.concurrent.locks.ReentrantLock;

public class Node<T> {

		T data;
		Node<T> next;
		ReentrantLock lock = new ReentrantLock();

		// Constructor
		Node(T d) {
				data = d;
		}

		void lock() {
			lock.lock();
		}

		void unlock() {
			lock.unlock();
		}
}

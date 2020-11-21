package lab4;

public class SyncQueue<T extends Comparable<T>> {
		private final QueueLinkedList<T> list = new QueueLinkedList<T>();
		public boolean active = true;
		public int elems = 0;

		public SyncQueue(){}

		public synchronized void addElement(T data) {
				list.insert(data);
				elems++;
		}

		public synchronized T removeElement() {
				Node<T> node = list.remove();
				if(node == null)
						return null;
				elems--;
				return node.data;
		}
}

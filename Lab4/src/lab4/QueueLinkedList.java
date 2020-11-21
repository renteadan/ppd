package lab4;

public class QueueLinkedList<T extends Comparable<T>> {

		Node<T> head;
		Node<T> last;

		QueueLinkedList() {
				last = head = new Node<T>(null);
		}

		public void insert(T data) {
				last.next = new Node<>(data);
				last = last.next;
		}

		public T remove() {
				head = head.next;
				T x = head.data;
				head.data = null;
				return x;
		}
}
package lab4;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SortedLinkedList<T extends Comparable<T>> {

		Node<T> head; // head of list

		// Linked list Node.
		// This inner class is made static
		// so that main() can access i

		// Method to insert a new node
		public synchronized void insert(T data) {
			Node<T> newNode = new Node<>(data);
			Node<T> current = head;
			Node<T> previous = null;
			while(current != null && data.compareTo(current.data) > 0){
				previous = current;
				current = current.next;
			}
			if(previous == null){
				head = newNode;
			} else {
					previous.next = newNode;
			}
			newNode.next = current;
		}

		public Node<T> getElement(int pos) {
				Node<T> current = head;
				if(current == null)
						return null;
				int i = 0;
				while(i != pos) {
						current = current.next;
						if(current == null)
								return null;
						i++;
				}
				return current;
		}

		public Vector<T> getList() {
				Vector<T> result = new Vector<>();
				Node<T> current = head;
				if(current == null)
						return result;
				while(current != null) {
						result.add(current.data);
						current = current.next;
				}
				return result;
		}
}
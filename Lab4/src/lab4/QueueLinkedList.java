package lab4;

public class QueueLinkedList<T extends Comparable<T>> {

		Node<T> head;
		Node<T> last;

		public void insert(T data) {
				Node<T> newNode = new Node<>(data);
				if(head == null) {
						head = newNode;
						last = newNode;
						return;
				}

				if(last == head) {
						last = newNode;
						head.next = last;
						return;
				}

				last.next = newNode;
				last = last.next;
		}

		public Node<T> remove(){
				if(head == null){
						return null;
				}
				Node<T> temp = head;
				head = head.next;
				return temp;
		}
}
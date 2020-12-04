package lab4;

public class MonomialLinkedList extends SortedLinkedList<Monomial> {

		@Override
		public Node<Monomial> getElement(int grade) {
				Node<Monomial> current = head;
				if(current == null)
						return null;
				synchronized (current) {
					while(current.data.grade < grade) {
						current = current.next;
						if(current == null)
							return null;
					}
					if(current.data.grade == grade)
						return current;
					return null;
				}
		}
}

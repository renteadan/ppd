package lab4;

public class MonomialLinkedList extends SortedLinkedList<Monomial> {

		@Override
		public synchronized Node<Monomial> getElement(int grade) {
				Node<Monomial> current = head;
				if(current == null)
						return null;
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

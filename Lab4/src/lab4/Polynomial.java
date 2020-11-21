package lab4;

import java.util.Vector;

public class Polynomial {
		private final MonomialLinkedList polynomial = new MonomialLinkedList();

		public Polynomial() {}

		public void addMonomial(Monomial monomial) {
				synchronized (polynomial) {
						Monomial mon = getMonomial(monomial.grade);
						if(mon == null) {
								polynomial.insert(monomial);
								return;
						}
						mon.coefficient += monomial.coefficient;
				}
		}

		private Monomial getMonomial(int grade) {
				synchronized (polynomial) {
						Node<Monomial> node = this.polynomial.getElement(grade);
						if(node == null)
								return null;
						return node.data;
				}
		}

		public Vector<Monomial> getPolynomial() {
				synchronized (polynomial) {
						return polynomial.getList();
				}
		}
}

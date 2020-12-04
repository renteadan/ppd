package lab4;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Polynomial {
		private HashMap<Integer, Monomial> storage = new HashMap<>();
		private int added = 0;

		public synchronized void increaseCount(int inc) {
			added += inc;
		}

		public int getCount() {
			return added;
		}

		public Polynomial() {}

		public void addMonomial(Monomial monomial) {
					Monomial mon = getMonomial(monomial.grade);
					if(mon == null) {
							storage.put(monomial.grade, monomial);
							return;
					}
					mon.coefficient += monomial.coefficient;
		}

		private Monomial getMonomial(int grade) {
			return storage.get(grade);
		}

		public Vector<Monomial> getPolynomial() {
			return new Vector<>(storage.values());
		}

		public synchronized void addToStorage(HashMap<Integer, Monomial> value) {
				storage =
						Stream.concat(storage.entrySet().stream(), value.entrySet().stream())
								.collect(Collectors.toMap(
										Entry::getKey,
										Entry::getValue,
										(el1, el2) -> {
											el1.coefficient += el2.coefficient;
											return el1;
										},
										HashMap::new
								));
		}
}

package lab4;

import java.util.Objects;

public class Monomial implements Comparable<Monomial> {
		int grade = 0;
		int coefficient = 0;

		public Monomial(int grade, int coefficient) {
				this.coefficient = coefficient;
				this.grade = grade;
		}

		@Override
		public int compareTo(Monomial o) {
				return grade - o.grade;
		}

		@Override
		public String toString() {
				return String.format("grade=%d coefficient=%d", grade, coefficient);
		}

		@Override
		public boolean equals(Object o) {
				if (this == o) return true;
				if (!(o instanceof Monomial)) return false;
				Monomial monomial = (Monomial) o;
				return grade == monomial.grade &&
						coefficient == monomial.coefficient;
		}

		@Override
		public int hashCode() {
				return Objects.hash(grade, coefficient);
		}
}

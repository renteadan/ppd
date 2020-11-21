package lab4;

import helpers.Helper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class SequentialRunner {
		private int nrPolynomials;
		private Polynomial polynomial = new Polynomial();
		public SequentialRunner(int nrPolynomials) {
				this.nrPolynomials = nrPolynomials;
		}

		private void readPolynomials() {
				for(int i = 0; i< nrPolynomials; i++) {
						readPolynomial("polynoms/polinom"+i+".txt");
				}
		}

		private void readPolynomial(String fileName) {
				try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
						String line;
						while ((line = br.readLine()) != null) {
								var elements = line.split(" ");
								int grade = Integer.parseInt(elements[0]);
								int coefficient = Integer.parseInt(elements[1]);
								polynomial.addMonomial(new Monomial(grade, coefficient));
						}
				} catch (IOException e) {
						e.printStackTrace();
				}
		}

		public Vector<Monomial> run() {
				readPolynomials();
				return polynomial.getPolynomial();
		}
}

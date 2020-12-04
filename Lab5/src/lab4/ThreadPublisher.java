package lab4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ThreadPublisher extends Thread {
		private String fileName;
		private final Exchange exchange;

		public void readPolynomial() {
				try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
						String line;
						while ((line = br.readLine()) != null) {
								var elements = line.split(" ");
								int grade = Integer.parseInt(elements[0]);
								int coefficient = Integer.parseInt(elements[1]);
								exchange.publishMessage(new Monomial(grade, coefficient));
						}
				} catch (IOException e) {
						e.printStackTrace();
				}
		}

		public ThreadPublisher(String fileName, Exchange exchange) {
				this.fileName = fileName;
				this.exchange = exchange;
		}

		public void run() {
			readPolynomial();
		}
}

package lab4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ThreadPublisher implements Runnable {
		private String fileName;
		private final SyncQueue<Monomial> queue;

		public void readPolynomial() {
				try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
						String line;
						while ((line = br.readLine()) != null) {
								var elements = line.split(" ");
								int grade = Integer.parseInt(elements[0]);
								int coefficient = Integer.parseInt(elements[1]);
								synchronized (queue) {
										queue.addElement(new Monomial(grade, coefficient));
								}
						}
				} catch (IOException e) {
						e.printStackTrace();
				}
		}

		public ThreadPublisher(String fileName, SyncQueue<Monomial> syncQueue) {
				this.fileName = fileName;
				this.queue = syncQueue;
		}

		public void run() {
				readPolynomial();
		}
}

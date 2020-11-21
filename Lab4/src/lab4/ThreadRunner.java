package lab4;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadRunner {
		private int nrPolynomials;
		private int consumerThreadsNumber;
		private ExecutorService publisherPool, consumerPool;
		private Vector<ThreadConsumer> consumerThreads;
		private IQueue<Monomial> queue;
		private Polynomial result;
		public ThreadRunner(int consumerThreadsNumber, int publisherThreadsNumber, int nrPolynomials, IQueue<Monomial> queue) {
				this.queue = queue;
				this.consumerThreadsNumber = consumerThreadsNumber;
				this.nrPolynomials = nrPolynomials;
				consumerPool = Executors.newFixedThreadPool(this.consumerThreadsNumber);
				publisherPool = Executors.newFixedThreadPool(publisherThreadsNumber);
		}

		private <T extends Thread> void waitThreads(Vector<T> threads) {
				for(Thread tr: threads){
						try {
								tr.join();
						} catch (InterruptedException ex) {
								System.out.println(ex.getMessage());
						}
				}
		}
		private void runConsumers() {
				consumerThreads = new Vector<>();
				for(int i = 0; i< consumerThreadsNumber; i++) {
						consumerThreads.add(new ThreadConsumer(result, queue));
						consumerThreads.get(i).start();
				}
		}

		private void runConsumersPool() {
				for(int i = 0; i< consumerThreadsNumber; i++) {
						ThreadConsumer runnable = new ThreadConsumer(result, queue);
						consumerPool.execute(runnable);
				}
				consumerPool.shutdown();
		}

		private void runPublishersPool() {
				for(int i = 0; i < nrPolynomials; i++) {
						ThreadPublisher publisher= new ThreadPublisher("polynoms/polinom"+i+".txt", queue);
						publisherPool.execute(publisher);
				}
				publisherPool.shutdown();
		}

		public Vector<Monomial> runPool() throws InterruptedException {
				result = new Polynomial();
				runPublishersPool();
				runConsumersPool();
				publisherPool.awaitTermination(10, TimeUnit.SECONDS);
				queue.setActive(false);
				consumerPool.awaitTermination(10, TimeUnit.SECONDS);
				return result.getPolynomial();
		}

		public Vector<Monomial> run() throws InterruptedException {
				result = new Polynomial();
				runPublishersPool();
				runConsumers();
				publisherPool.awaitTermination(10, TimeUnit.SECONDS);
				queue.setActive(false);
				waitThreads(consumerThreads);
				return result.getPolynomial();
		}

}

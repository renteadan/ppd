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
		private Exchange exchange;
		private Polynomial result;
		public ThreadRunner(int consumerThreadsNumber, int publisherThreadsNumber, int nrPolynomials, Exchange exchange) {
				this.exchange = exchange;
				this.consumerThreadsNumber = consumerThreadsNumber;
				this.nrPolynomials = nrPolynomials;
				consumerPool = Executors.newFixedThreadPool(consumerThreadsNumber);
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
						ThreadConsumer consumer = new ThreadConsumer(result);
						consumerThreads.add(consumer);
						consumerThreads.get(i).start();
						exchange.subscribe(consumer);
				}
		}

		private void runConsumersPool() {
				for(int i = 0; i< consumerThreadsNumber; i++) {
						ThreadConsumer runnable = new ThreadConsumer(result);
						exchange.subscribe(runnable);
						consumerPool.execute(runnable);
				}
				consumerPool.shutdown();
		}

		private void runPublishersPool() {
				for(int i = 0; i < nrPolynomials; i++) {
						ThreadPublisher publisher= new ThreadPublisher("polynoms/polinom"+i+".txt", exchange);
						publisherPool.execute(publisher);
				}
				publisherPool.shutdown();
		}

		public Vector<Monomial> runPool() throws InterruptedException {
			result = new Polynomial();
			runPublishersPool();
			runConsumersPool();
			publisherPool.awaitTermination(10, TimeUnit.SECONDS);
			if(!publisherPool.isTerminated()) {
				publisherPool.shutdownNow();
			}
			System.out.println("Finish publish");
			exchange.setInactive();
			consumerPool.awaitTermination(10, TimeUnit.SECONDS);
			if(!consumerPool.isTerminated()) {
				System.out.println("Pool not finished!");
				consumerPool.shutdownNow();
			}
			System.out.println("Finish consuming");
			return result.getPolynomial();
		}

		public Vector<Monomial> run() throws InterruptedException {
			result = new Polynomial();
			runPublishersPool();
			runConsumers();
			publisherPool.awaitTermination(10, TimeUnit.SECONDS);
			System.out.println("Finish publish");
			exchange.setInactive();
			waitThreads(consumerThreads);
			System.out.println("Finish consuming");
			return result.getPolynomial();
		}

}

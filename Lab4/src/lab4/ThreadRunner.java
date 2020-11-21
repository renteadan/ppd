package lab4;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadRunner {
		private int nrPolynomials;
		private int consumerThreadsNumber, publisherThreadsNumber;
		private Vector<ThreadPublisher> publishThreads;
		private ExecutorService readPool;
		private Vector<ThreadConsumer> consumerThreads;
		private SyncQueue<Monomial> queue;
		private Polynomial result;
		public ThreadRunner(int consumerThreads, int publisherThreadsNumber, int nrPolynomials) {
				this.consumerThreadsNumber = consumerThreads;
				this.publisherThreadsNumber = publisherThreadsNumber;
				this.nrPolynomials = nrPolynomials;
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

		private void runPublishers() {
//				publishThreads = new Vector<>();
				readPool = Executors.newFixedThreadPool(publisherThreadsNumber);
				for(int i = 0; i< nrPolynomials; i++) {
						ThreadPublisher publisher= new ThreadPublisher("polynoms/polinom"+i+".txt", queue);
						readPool.execute(publisher);
				}
		}

		public Vector<Monomial> run() {
				queue = new SyncQueue<>();
				result = new Polynomial();
				runPublishers();
				runConsumers();
//				waitThreads(publishThreads);
				readPool.shutdown();
				queue.active = false;
				waitThreads(consumerThreads);
				return result.getPolynomial();
		}

}

package restaurant.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import restaurant.model.OrderStatus;
import restaurant.model.Order;
import restaurant.util.Toolkit;

/**
 * Kitchen manages a pool of chefs and an order queue. Orders are taken from the
 * queue and processed asynchronously.
 */
public class Kitchen {
	private final ExecutorService chefPool;
	private final BlockingQueue<Order> orderQueue = new LinkedBlockingQueue<>();
	private final int numberOfChefs;

	/**
	 * Create a kitchen with a fixed number of chefs.
	 * 
	 * @param numberOfChefs how many chefs to run in parallel
	 */
	public Kitchen(int numberOfChefs) {
		this.numberOfChefs = numberOfChefs;
		this.chefPool = Executors.newFixedThreadPool(numberOfChefs);

		// Start chefs
		for (int i = 0; i < numberOfChefs; i++) {
			chefPool.submit(new Chef(orderQueue));
		}
	}

	/**
	 * Accept an order and put it into the queue. Returns a CompletableFuture
	 * representing the asynchronous preparation.
	 */
	public CompletableFuture<Order> acceptOrder(Order order) {
		CompletableFuture<Order> future = new CompletableFuture<>();
		order.setFuture(future);
		try {
			orderQueue.put(order);
			order.setStatus(OrderStatus.IN_PREPARATION);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Could not accept order", e);
		}
		return future;
	}

	/**
	 * Close the kitchen by sending poison pills and shutting down the thread pool.
	 */
	public void close() {
		Toolkit.logTime.accept("Closing kitchen...");

		sendPoisonPills();

		chefPool.shutdown();
		try {
			if (!chefPool.awaitTermination(1, TimeUnit.SECONDS)) {
				chefPool.shutdownNow();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			chefPool.shutdownNow();
		}

		Toolkit.logTime.accept("Kitchen closed!");
	}

	/**
	 * Send one poison pill for each chef to stop their infinite loop.
	 */
	private void sendPoisonPills() {
		for (int i = 0; i < numberOfChefs; i++) {
			try {
				orderQueue.put(Order.poisonPill());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				System.err.println("Error sending poison pill: " + e.getMessage());
			}
		}
	}

	/** @return true if the kitchen is fully closed */
	public boolean isClosed() {
		return chefPool.isTerminated();
	}
}

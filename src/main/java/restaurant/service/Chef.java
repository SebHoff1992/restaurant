package restaurant.service;

import java.util.concurrent.BlockingQueue;

import restaurant.model.OrderStatus;
import restaurant.model.Order;
import restaurant.model.Preparation;
import restaurant.util.Toolkit;

/**
 * A chef takes orders from the queue and prepares them.
 */
public class Chef implements Runnable {
	private final BlockingQueue<Order> queue;

	public Chef(BlockingQueue<Order> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Order order = queue.take();
				if (!Toolkit.isValidOrder.test(order)) {
					Toolkit.logTime.accept("Chef ends his shift.");
					break;
				}

				long estimatedDuration = Toolkit.estimatedDuration.apply(order);
				Toolkit.logger.accept(order, "Start processing (estimated: " + estimatedDuration + " ms)");

				Preparation task = new Preparation(order, estimatedDuration);

				long start = System.currentTimeMillis();
				try {
					task.call();
					long end = System.currentTimeMillis();
					long actualDuration = end - start;
					order.setStatus(OrderStatus.PREPARED);
					Toolkit.logger.accept(order, "Order completed in " + actualDuration + " ms");

					// Complete the future successfully
					order.getFuture().complete(order);

				} catch (Exception e) {
					order.getFuture().completeExceptionally(e);
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}

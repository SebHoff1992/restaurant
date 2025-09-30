package restaurant.model;

import static restaurant.util.Toolkit.*;

import java.util.concurrent.Callable;

/**
 * Represents the preparation of an order in the kitchen. Each dish is processed
 * sequentially with a fixed duration.
 */
public class Preparation implements Callable<Order> {
	private final Order order;
	private final long durationPerDish;

	/**
	 * Create a preparation task for an order.
	 * 
	 * @param order           the order to be prepared
	 * @param durationPerDish the time (in ms) needed to prepare each dish
	 */
	public Preparation(Order order, long durationPerDish) {
		this.order = order;
		this.durationPerDish = durationPerDish;
	}

	/**
	 * Prepares all dishes in the order. Each dish takes {@code durationPerDish}
	 * milliseconds.
	 * 
	 * @return the completed order
	 */
	@Override
	public Order call() throws Exception {
		for (Dish dish : order.getDishes()) {
			Thread.sleep(durationPerDish);
			logger.accept(order, "Dish prepared: " + dish);
		}
		return order;
	}
}

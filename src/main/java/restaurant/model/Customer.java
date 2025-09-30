package restaurant.model;

import java.util.concurrent.CompletableFuture;

import restaurant.payment.Payment;
import restaurant.service.Waiter;
import restaurant.util.Toolkit;

/**
 * Represents a restaurant customer. A customer can place an order and pay using
 * different payment methods.
 */
public class Customer {
	private final String name;
	private final int tableNumber;
	private Order order;

	/**
	 * Create a new customer with a name and table number.
	 * 
	 * @param name        The customer's name
	 * @param tableNumber The table assigned to the customer
	 */
	public Customer(String name, int tableNumber) {
		this.name = name;
		this.tableNumber = tableNumber;
	}

	/**
	 * Place an order with the waiter. The order is recorded by the customer and
	 * sent to the waiter for processing.
	 * 
	 * @param waiter The waiter handling the order
	 * @param order  The order placed by the customer
	 * @return CompletableFuture representing the asynchronous order processing
	 */
	public CompletableFuture<Order> placeOrder(Waiter waiter, Order order) {
		this.order = order;
		return waiter.takeOrder(this, order);
	}

	/**
	 * Pay for the order using the given payment method. If no order exists, the
	 * payment is rejected.
	 * 
	 * @param waiter  The waiter handling the payment
	 * @param payment The payment method (e.g., cash, card)
	 */
	public void pay(Waiter waiter, Payment payment) {
		if (order == null) {
			Toolkit.logTime.accept(name + " has not ordered anything and cannot pay.");
			return;
		}
		Toolkit.logTime.accept(name + " wants to pay.");
		waiter.processPayment(this, order, payment);
	}

	/** @return The table number assigned to the customer */
	public int getTableNumber() {
		return tableNumber;
	}

	/** @return The customer's order, or null if none exists */
	public Order getOrder() {
		return order;
	}

	/** @return The customer's name */
	public String getName() {
		return name;
	}

	/** Assign or replace the customer's order */
	public void setOrder(Order order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return String.format("Customer [Name: %s | Table %d | Order: %s]", name, tableNumber,
				order != null ? String.format("Amount: %.2f € | Status: %s", order.getTotalPrice(),
						order.getStatus().toString().toLowerCase()) : "no order");
	}
}

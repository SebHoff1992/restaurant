package restaurant.service;

import java.util.concurrent.CompletableFuture;

import restaurant.model.Order;
import restaurant.model.Customer;
import restaurant.payment.Payment;
import restaurant.payment.intern.CashRegister;
import restaurant.util.Toolkit;

/**
 * Waiter acts as the interface between customer, kitchen, and cash register.
 */
public class Waiter {

	private final Kitchen kitchen;
	private final CashRegister cashRegister;

	public Waiter(Kitchen kitchen, CashRegister cashRegister) {
		this.kitchen = kitchen;
		this.cashRegister = cashRegister;
	}

	/**
	 * Take an order from a customer and forward it to the kitchen.
	 */
	public CompletableFuture<Order> takeOrder(Customer customer, Order order) {
		Toolkit.logTime.accept("Waiter takes order from customer " + customer.getName());
		return kitchen.acceptOrder(order);
	}

	/**
	 * Process a customer's payment for a given order.
	 */
	public void processPayment(Customer customer, Order order, Payment payment) {
		Toolkit.logTime.accept("Waiter processes payment for customer " + customer.getName());

		boolean success = cashRegister.pay(order, payment);
		if (!success) {
			Toolkit.logTime.accept("Payment failed. Call the manager...");
			// error handling could be extended here
		}
	}
}

package restaurant.core.waiter.model;

import java.util.concurrent.CompletableFuture;

import restaurant.core.customer.model.Customer;
import restaurant.core.kitchen.model.Kitchen;
import restaurant.core.order.model.Order;
import restaurant.infrastructure.util.Toolkit;
import restaurant.payment.model.CashRegister;
import restaurant.payment.model.Payment;

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
		if (order.getStatus().isFinalized()) {
			Toolkit.logger.accept(order, "Payment attempt ignored: Order already finalized.");
			return;
		}
		if (!order.getStatus().canBePaid()) {
			Toolkit.logger.accept(order, "Payment attempt rejected: Order not ready yet (" + order.getStatus() + ")");
			return;
		}

		boolean success = cashRegister.pay(order, payment);
		if (!success) {
			Toolkit.logTime.accept("Payment failed. Call the manager...");
			// error handling could be extended here
		}
	}
}

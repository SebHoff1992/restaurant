package restaurant.payment.model;

import restaurant.core.order.model.Order;
import restaurant.payment.model.PaymentMethod;

/**
 * Payment method representing cash payment.
 */
public final class CashPayment extends PaymentMethod {

	/**
	 * Create a new CashPayment instance.
	 * 
	 * @param amount the payment amount
	 */
	public CashPayment(double amount) {
		super(amount);
	}

	/**
	 * Always succeeds for cash payments in this simulation.
	 */
	@Override
	public boolean pay(Order order) {
		return true;
	}
}

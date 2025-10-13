package restaurant.payment;

import restaurant.model.Order;
import restaurant.payment.PaymentMethod;

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

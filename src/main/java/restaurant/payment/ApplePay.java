package restaurant.payment;

import restaurant.model.Order;
import restaurant.payment.PaymentMethod;

/**
 * Payment method representing Apple Pay.
 */
public final class ApplePay extends PaymentMethod {

	/**
	 * Create a new ApplePay instance.
	 * 
	 * @param amount the payment amount
	 */
	public ApplePay(double amount) {
		super(amount);
	}

	/**
	 * Attempts to pay for the given order.
	 * 
	 * @param order the order to pay
	 * @return true if successful, false otherwise
	 */
	@Override
	public boolean pay(Order order) {
		return false; // not yet implemented
	}
}

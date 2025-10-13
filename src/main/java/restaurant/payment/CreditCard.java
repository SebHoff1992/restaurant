package restaurant.payment;

import restaurant.model.Order;
import restaurant.payment.PaymentMethod;

/**
 * Generic credit card payment method. Specific card types (e.g. Visa) extend
 * this class.
 */
public non-sealed class CreditCard extends PaymentMethod {

	/**
	 * Create a new credit card payment.
	 * 
	 * @param amount the payment amount
	 */
	public CreditCard(double amount) {
		super(amount);
	}

	/**
	 * Attempts to pay with a credit card.
	 * 
	 * @return false in this simulation (not implemented)
	 */
	@Override
	public boolean pay(Order order) {
		return false;
	}
}

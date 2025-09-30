package restaurant.payment.intern;

import restaurant.model.Order;
import restaurant.payment.PaymentMethod;

/**
 * Generic mobile payment option (e.g. Google Pay).
 */
public non-sealed class MobilePayment extends PaymentMethod {

	public MobilePayment(double amount) {
		super(amount);
	}

	@Override
	public boolean pay(Order order) {
		return false;
	}
}

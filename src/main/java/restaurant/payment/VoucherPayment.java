package restaurant.payment;

import restaurant.model.Order;
import restaurant.payment.Payment;

/**
 * Payment method using a voucher.
 */
public final class VoucherPayment implements Payment {

	/**
	 * Attempts to pay with a voucher.
	 * 
	 * @param order the order to pay
	 * @return false in this simulation (not implemented)
	 */
	@Override
	public boolean pay(Order order) {
		return false;
	}
}

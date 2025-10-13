package restaurant.payment;

import restaurant.model.OrderStatus;
import restaurant.model.Order;
import restaurant.payment.Payment;
import restaurant.util.Toolkit;

/**
 * Represents the cash register where payments are finalized.
 */
public final class CashRegister {

	/**
	 * Process a payment for a given order. Updates the order status and logs the
	 * result.
	 * 
	 * @param order   the order to finalize
	 * @param payment the payment method used
	 * @return true if payment succeeded, false otherwise
	 */
	public boolean pay(Order order, Payment payment) {
		Toolkit.logger.accept(order, "Cash register: finalizing payment...");
		boolean success = payment.pay(order);
		String msg;
		if (success) {
			order.setStatus(OrderStatus.PAID);
			msg = "Cash register: payment completed successfully.";
		} else {
			order.setStatus(OrderStatus.PAYMENT_FAILED);
			msg = "Cash register: payment failed.";
		}
		Toolkit.logger.accept(order, msg);
		return success;
	}
}

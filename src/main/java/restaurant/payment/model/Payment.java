package restaurant.payment.model;

import restaurant.core.order.model.Order;

/**
 * Sealed interface for different payment methods. Only the permitted subtypes
 * are allowed to implement this interface.
 */
public sealed interface Payment permits VoucherPayment, PaymentMethod {

	/**
	 * Process the payment for a given order.
	 * 
	 * @param order the order to be paid
	 * @return true if the payment was successful, false otherwise
	 */
	boolean pay(Order order);
}

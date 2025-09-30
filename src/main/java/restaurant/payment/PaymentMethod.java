package restaurant.payment;

import restaurant.payment.intern.ApplePay;
import restaurant.payment.intern.CashPayment;
import restaurant.payment.intern.CreditCard;
import restaurant.payment.intern.MobilePayment;

/**
 * Base class for different payment methods. This is a sealed abstract class,
 * allowing only specific implementations.
 */
public sealed abstract class PaymentMethod implements Payment permits CreditCard, CashPayment, MobilePayment, ApplePay {

	protected double amount;

	/**
	 * Create a new payment method with the given amount.
	 * 
	 * @param amount the payment amount
	 */
	public PaymentMethod(double amount) {
		this.amount = amount;
	}
}

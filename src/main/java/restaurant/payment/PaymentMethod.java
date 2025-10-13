package restaurant.payment;

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

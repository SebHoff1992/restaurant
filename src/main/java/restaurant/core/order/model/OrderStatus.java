package restaurant.core.order.model;

/**
 * Represents the possible states of an order in the restaurant.
 */
public enum OrderStatus {
	OPEN, // Order has been placed but not yet processed
	IN_PREPARATION, // Kitchen is working on the order
	PREPARED, // Kitchen finished preparing the order
//	SERVED, // Customer has received the food #STILL NEED IMPLEMENTATION
	PAYMENT_FAILED, // Payment attempt failed
	PAID; // Payment completed successfully

	/** Returns true if payment is allowed in this state. */
	public boolean canBePaid() {
		return this == PREPARED;
	}

	/** Returns true if order is already finished (no further actions allowed) */
	public boolean isFinalized() {
		return this == PAID;
	}
}

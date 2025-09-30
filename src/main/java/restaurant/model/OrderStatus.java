package restaurant.model;

/**
 * Represents the possible states of an order in the restaurant.
 */
public enum OrderStatus {
	OPEN, // Order has been placed but not yet processed
	IN_PREPARATION, // Kitchen is working on the order
	PREPARED, // Kitchen finished preparing the order
	SERVED, // Customer has received the food
	PAYMENT_FAILED, // Payment attempt failed
	PAID, // Payment completed successfully
	COMPLETED // Order fully completed
}

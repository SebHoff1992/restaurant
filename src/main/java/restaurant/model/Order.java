package restaurant.model;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import restaurant.util.Toolkit;

/**
 * Represents an order placed by a customer at a specific table. Contains the
 * ordered dishes, current status, and an optional future for async processing.
 */
public class Order {
	private final int tableNumber; // mandatory field
	private Customer customer;
	private final List<Dish> dishes;
	private OrderStatus status = OrderStatus.OPEN;
	private CompletableFuture<Order> future;

	/**
	 * Private constructor used internally to create orders.
	 * 
	 * @param table    the table number
	 * @param customer the customer who placed the order
	 * @param dishes   the list of ordered dishes
	 */
	private Order(int table, Customer customer, List<Dish> dishes) {
		this.tableNumber = table;
		this.customer = Objects.requireNonNull(customer, "Customer must not be null");
		this.dishes = Objects.requireNonNull(dishes, "Dishes must not be null");

		// Only log normal orders, not poison pills
		if (table != -1) {
			Toolkit.logger.accept(this, "New order created: " + this);
		}
	}

	/**
	 * Factory method to create a new order. Also links the order to the customer.
	 */
	public static Order create(Customer customer, List<Dish> dishes) {
		Order order = new Order(customer.getTableNumber(), customer, dishes);
		customer.setOrder(order);
		return order;
	}

	/**
	 * Special order used as a poison pill to stop processing threads.
	 */
	public static Order poisonPill() {
		return new Order(-1, new Customer("POISON", -1), List.of());
	}

	public void setFuture(CompletableFuture<Order> future) {
		this.future = future;
	}

	public CompletableFuture<Order> getFuture() {
		return future;
	}

	public int getTableNumber() {
		return tableNumber;
	}

	public Customer getCustomer() {
		return customer;
	}

	public List<Dish> getDishes() {
		return dishes;
	}

	/**
	 * @return the total price of all dishes in this order
	 */
	public double getTotalPrice() {
		return dishes.stream().mapToDouble(Dish::price).sum();
	}

	public OrderStatus getStatus() {
		return status;
	}

	/**
	 * Update the order status and log the change.
	 */
	public void setStatus(OrderStatus status) {
		Toolkit.logTime.accept("Status changed: " + status.toString().toLowerCase());
		this.status = status;
	}

	@Override
	public String toString() {
		return String.format("Order [Table %d | Customer: %s | Dishes: %s | Total: %.2f € | Status: %s]", tableNumber,
				customer.getName(), dishes.stream().map(Dish::name).toList(), getTotalPrice(),
				status.toString().toLowerCase());
	}
}

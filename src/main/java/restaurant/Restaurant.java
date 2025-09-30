package restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import restaurant.model.OrderStatus;
import restaurant.model.Order;
import restaurant.model.Dish;
import restaurant.model.Category;
import restaurant.model.Customer;
import restaurant.model.Menu;
import restaurant.payment.intern.CashPayment;
import restaurant.payment.intern.CashRegister;
import restaurant.service.Kitchen;
import restaurant.service.Waiter;
import restaurant.util.Toolkit;

/**
 * Main entry point of the restaurant simulation. Coordinates kitchen, waiter,
 * customers, and payments.
 */
public class Restaurant {

	private final Kitchen kitchen;
	private final Waiter waiter;
	private final CashRegister cashRegister;

	/** Static demo menu for the restaurant */
	public static Menu MENU = new Menu(
			List.of(new Dish("Pizza", Category.MAIN_COURSE, 8.50), new Dish("Burger", Category.MAIN_COURSE, 7.90),
					new Dish("Salad", Category.STARTER, 4.50), new Dish("Pasta", Category.MAIN_COURSE, 9.20),
					new Dish("Smoothie", Category.DRINK, 3.80), new Dish("Soup", Category.STARTER, 4.00),
					new Dish("Ice Cream", Category.DESSERT, 3.50), new Dish("Coffee", Category.DRINK, 2.50)));

	private final List<CompletableFuture<Order>> orders = new ArrayList<>();

	/**
	 * Create a new restaurant with a kitchen, cash register, and waiter.
	 * 
	 * @param numChefs number of chefs working in the kitchen
	 */
	public Restaurant(int numChefs) {
		this.kitchen = new Kitchen(numChefs);
		this.cashRegister = new CashRegister();
		this.waiter = new Waiter(kitchen, cashRegister);
	}

	/**
	 * Start the restaurant with a demo customer and one order. Demonstrates the
	 * full flow: ordering → preparation → payment → reporting.
	 */
	public void start() {
		try {
			Customer customer = new Customer("Peter", 4);
			Order order = Toolkit.testOrderWithCustomer.apply(customer);

			// Place order with waiter and receive a Future for the kitchen
			CompletableFuture<Order> future = customer.placeOrder(waiter, order)
					// After preparation → directly pay
					.thenApply(preparedOrder -> {
						preparedOrder.getCustomer().pay(waiter, new CashPayment(preparedOrder.getTotalPrice()));
						return preparedOrder; // Future completes only after payment
					});
			orders.add(future);

			// Wait for all orders to complete
			CompletableFuture<Void> allFinished = CompletableFuture.allOf(orders.toArray(new CompletableFuture[0]));

			allFinished.thenRun(() -> {
				if (allOrdersCompleted()) {
					Toolkit.logTime.accept("All orders paid → restaurant closes!");
				} else {
					Toolkit.logTime.accept("Not all orders were paid!");
				}
				kitchen.close();
				printReport();
			}).join();

		} catch (Exception e) {
			throw new RuntimeException("Error during restaurant operation", e);
		}
		Toolkit.logTime.accept("Restaurant closed!");
	}

	/**
	 * @return true if all orders are completed and marked as PAID
	 */
	private boolean allOrdersCompleted() {
		return orders.stream().map(CompletableFuture::join).allMatch(b -> b.getStatus() == OrderStatus.PAID);
	}

	/**
	 * Print a daily report with all orders, their statuses, and totals.
	 */
	public void printReport() {
		String title = "Daily Report";
		String line = "-".repeat(title.length());

		String header = line + "\n" + title + "\n" + line;
		System.out.println(header);

		double totalRevenue = 0.0;
		double totalPaid = 0.0;

		for (CompletableFuture<Order> cf : orders) {
			try {
				Order finishedOrder = cf.get();
				String statusMessage = switch (finishedOrder.getStatus()) {
				case PAID -> {
					totalPaid += finishedOrder.getTotalPrice();
					yield "Order successfully completed.";
				}
				case PAYMENT_FAILED -> "Outstanding payment!";
				case PREPARED -> "Not yet paid.";
				default -> "Unknown status!";
				};
				// Indent order details for better readability
				String details = finishedOrder.toString().indent(4);
				Toolkit.logger.accept(finishedOrder, statusMessage + "\n" + details);
				totalRevenue += finishedOrder.getTotalPrice();
			} catch (Exception e) {
				System.err.println("Error retrieving order: " + e.getMessage());
			}
		}

		String footer = """
				----------------------
				Total revenue:   %.2f €
				Total paid:      %.2f €
				----------------------
				""".formatted(totalRevenue, totalPaid);

		footer.lines().map(String::stripTrailing).forEach(System.out::println);
	}

	/**
	 * Find a customer by table number from a given list of customers.
	 */
	public Optional<Customer> findCustomerByTable(int tableNumber, List<Customer> customers) {
		return customers.stream().filter(c -> c.getTableNumber() == tableNumber).findFirst();
	}

	/**
	 * @return list of all customers who placed orders
	 */
	public List<Customer> getCustomers() {
		return orders.stream().map(CompletableFuture::join).map(Order::getCustomer).collect(Collectors.toList());
	}

	/**
	 * Main method to run the simulation.
	 */
	public static void main(String[] args) {
		new Restaurant(2).start();
	}
}

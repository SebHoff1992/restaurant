package restaurant.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import restaurant.core.customer.model.Customer;
import restaurant.core.kitchen.model.Kitchen;
import restaurant.core.model.Category;
import restaurant.core.model.Dish;
import restaurant.core.model.Menu;
import restaurant.core.order.model.Order;
import restaurant.core.order.model.OrderStatus;
import restaurant.core.waiter.model.Waiter;
import restaurant.infrastructure.util.Toolkit;
import restaurant.payment.model.CashPayment;
import restaurant.payment.model.CashRegister;

/**
 * Coordinates kitchen, waiter, customers, and payments.
 */
public class Restaurant {

	private final Kitchen kitchen;
	private final Waiter waiter;
	private final CashRegister cashRegister;
	private final List<Customer> customers = new ArrayList<>();
	private final List<CompletableFuture<Order>> orders = new ArrayList<>();
	public static final int MAX_CUSTOMERS = 20;
	private final List<Integer> freeTables = IntStream.rangeClosed(1, MAX_CUSTOMERS).boxed()
			.collect(Collectors.toList());

	/** Static demo menu for the restaurant */
	public static Menu MENU = new Menu(
			List.of(new Dish("Pizza", Category.MAIN_COURSE, 8.50), new Dish("Burger", Category.MAIN_COURSE, 7.90),
					new Dish("Salad", Category.STARTER, 4.50), new Dish("Pasta", Category.MAIN_COURSE, 9.20),
					new Dish("Smoothie", Category.DRINK, 3.80), new Dish("Soup", Category.STARTER, 4.00),
					new Dish("Ice Cream", Category.DESSERT, 3.50), new Dish("Coffee", Category.DRINK, 2.50)));

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
	 * Simulates customers entering the restaurant asynchronously. Each customer
	 * enters after a random 1–5 second delay and places an order shortly after 1-3
	 * seconds of delay.
	 */
	public void simulateCustomerEnters(int numCustomers) {
		try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
			IntStream.range(1, numCustomers + 1).forEach(i -> {
				executor.submit(() -> {
					try {
						// random 1–5s delay before entering
						Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5001));
						Customer c = Toolkit.createCustomerForFreeTable.apply(freeTables);
						if (c == null) {
							Toolkit.logTime.accept("Customer leaves — no tables available.");
							return;
						}
						customers.add(c);
						Toolkit.logTime
								.accept(c.getName() + " enters the restaurant and sits at table " + c.getTableNumber());
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				});
			});
		}
	}

	/** Simulates one customer leaving the restaurant (after random delay) */
	public void simulateCustomerExits(Customer customer) {
		try {
			Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3001)); // wait 1–3s before leaving
			Toolkit.logTime.accept(customer.getName() + " stands up and leaves the restaurant.");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/** Simulates all customers leaving (each in their own Virtual Thread) */
	public void simulateAllCustomersExit() {
		Toolkit.logTime.accept("Customers are starting to leave the restaurant...");
		try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
			for (Customer c : customers) {
				executor.submit(() -> simulateCustomerExits(c));
			}
		}
		Toolkit.logTime.accept("All customers have left (or are on their way out).");
		customers.clear();
	}

	/**
	 * Simulates customers placing orders at random intervals and paying after order
	 * is received.
	 */
	public void simulateOrders() {
		try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
			for (Customer c : customers) {
				executor.submit(() -> simulateOrder(c));
			}
		}
	}

	/** Simulates a single customer's order process including payment. */
	public void simulateOrder(Customer customer) {
		try {
			// Wait randomly 1–3 seconds before ordering
			Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3001));

			Order order = Toolkit.testOrderWithCustomer.apply(customer);
			Toolkit.logTime.accept(customer.getName() + " is placing an order...");

			CompletableFuture<Order> future = customer.placeOrder(waiter, order).thenApply(prepared -> {
				Toolkit.logTime.accept(customer.getName() + " received the order.");
				simulatePayment(customer);
				return prepared;
			});

			orders.add(future);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/** Customers pay their bills at random intervals (after ordering) */
	public void simulatePayments() {
		try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
			for (Customer c : customers) {
				executor.submit(() -> simulatePayment(c));
			}
		}
	}

	/** Simulates payment after a random delay */
	public void simulatePayment(Customer customer) {
		try {

			Thread.sleep(ThreadLocalRandom.current().nextInt(5000, 8001));
			Toolkit.logTime.accept(customer.getName() + " wants to pay the bill.");
			customer.pay(waiter, new CashPayment(customer.getOrder().getTotalPrice()));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/** Waits for all orders to complete and closes the kitchen. */
	public void close() {
		Toolkit.logTime.accept("Waiting for all customers to finish...");
		CompletableFuture.allOf(orders.toArray(new CompletableFuture[0])).join();
		Toolkit.logTime.accept("All customers finished!");
		kitchen.close();
	}

	/**
	 * Generates the daily report as a formatted String.
	 *
	 * @return formatted report text
	 */
	public String getReport() {
		StringBuilder sb = new StringBuilder();

		String title = "Daily Report";
		String line = "-".repeat(title.length());

		// --- Header ---
		sb.append(line).append("\n").append(title).append("\n").append(line).append("\n\n");

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

				// Append each order to the report
				sb.append("• ").append(statusMessage).append("\n");
				sb.append(finishedOrder.toString().indent(4)).append("\n");

				totalRevenue += finishedOrder.getTotalPrice();
			} catch (Exception e) {
				sb.append("⚠️ Error retrieving order: ").append(e.getMessage()).append("\n");
			}
		}

		// --- Footer ---
		sb.append("----------------------\n").append(String.format("Total revenue:   %.2f €%n", totalRevenue))
				.append(String.format("Total paid:      %.2f €%n", totalPaid)).append("----------------------\n");
		return sb.toString();
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

	/** Prints daily report */
	public void printReport2() {
		System.out.println(getReport());
	}

	/**
	 * @return true if all orders are completed and marked as PAID
	 */
	private boolean allOrdersCompleted() {
		return orders.stream().map(CompletableFuture::join).allMatch(b -> b.getStatus() == OrderStatus.PAID);
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
}

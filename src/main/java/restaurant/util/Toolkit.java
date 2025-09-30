package restaurant.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import restaurant.Restaurant;
import restaurant.model.Order;
import restaurant.model.Dish;
import restaurant.model.Customer;

/**
 * Collection of reusable functional interfaces and helpers for the restaurant
 * project.
 */
public class Toolkit {

	/** Formatter for timestamps (mm:ss.SSS) */
	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("mm:ss.SSS");

	/** Supplies the current time as formatted string */
	public static final Supplier<String> now = () -> LocalDateTime.now().format(FORMATTER);

	/**
	 * TriConsumer – accepts three arguments without return value.
	 */
	@FunctionalInterface
	public interface TriConsumer<T, U, V> {
		void accept(T t, U u, V v);

		default TriConsumer<T, U, V> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
			return (t, u, v) -> {
				this.accept(t, u, v);
				after.accept(t, u, v);
			};
		}
	}

	/** Logs a message prefixed with the current time */
	public static final Consumer<String> logTime = (s) -> {
		System.out.printf("%s: %s%n", now.get(), s);
	};

	/**
	 * Logger for orders: prints current time, table number, and message. Example:
	 * "09:32.111: Table 5 | Order received"
	 */
	public static final BiConsumer<Order, String> logger = (o, msg) -> {
		if (o.getTableNumber() != -1) {
			String time = now.get();
			System.out.printf("%s: Table %d | %s%n", time, o.getTableNumber(), msg);
		}
	};

	/** Printer for orders with an additional message */
	public static final BiConsumer<Order, String> printer = (o, msg) -> {
		if (o.getTableNumber() != -1) {
			System.out.println(msg + "\n " + o);
		}
	};

	/**
	 * Example printer with an additional flag whether to log to file.
	 */
	public static final TriConsumer<Order, String, Boolean> printerLog = (o, msg, logToFile) -> {
		if (o.getTableNumber() != -1) {
			String output = msg + " for table " + o.getTableNumber();
			if (logToFile) {
				System.out.println("[FILE] " + output);
			} else {
				System.out.println(output);
			}
		}
	};

	/** Predicate: checks if an order is valid (not a poison pill). */
	public static final Predicate<Order> isValidOrder = o -> o != null && o.getTableNumber() != -1;

	/** Function: estimates the preparation duration of an order */
	public static final Function<Order, Long> estimatedDuration = o -> {
		int numberOfDishes = o.getDishes().size();
		long durationPerDish = 1000L + 300L * (numberOfDishes - 1);
		return durationPerDish * numberOfDishes;
	};

	/**
	 * Supplier: creates a random test order with 1–3 dishes and a random table
	 * number (1–20). Also creates a test customer linked to that order.
	 */
	public static final Supplier<Order> testOrder = () -> {
		int table = ThreadLocalRandom.current().nextInt(1, 21);
		int numberOfDishes = ThreadLocalRandom.current().nextInt(1, 4);

		List<Dish> dishes = new ArrayList<>();
		for (int i = 0; i < numberOfDishes; i++) {
			int index = ThreadLocalRandom.current().nextInt(Restaurant.MENU.getAllDishes().size());
			dishes.add(Restaurant.MENU.getAllDishes().get(index));
		}

		Customer customer = new Customer("TestCustomer-" + table, table);
		return Order.create(customer, dishes);
	};

	/**
	 * Function: creates a random test order with 1–3 dishes for an existing
	 * customer.
	 */
	public static final Function<Customer, Order> testOrderWithCustomer = (customer) -> {
		int numberOfDishes = ThreadLocalRandom.current().nextInt(1, 4);

		List<Dish> dishes = new ArrayList<>();
		for (int i = 0; i < numberOfDishes; i++) {
			int index = ThreadLocalRandom.current().nextInt(Restaurant.MENU.getAllDishes().size());
			dishes.add(Restaurant.MENU.getAllDishes().get(index));
		}

		return Order.create(customer, dishes);
	};
}

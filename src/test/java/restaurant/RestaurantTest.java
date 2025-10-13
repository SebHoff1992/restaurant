package restaurant;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import restaurant.core.customer.model.Customer;
import restaurant.core.kitchen.model.Kitchen;
import restaurant.core.model.Category;
import restaurant.core.model.Dish;
import restaurant.core.model.Menu;
import restaurant.core.model.Preparation;
import restaurant.core.model.Restaurant;
import restaurant.core.order.model.Order;
import restaurant.core.order.model.OrderStatus;
import restaurant.core.waiter.model.Waiter;
import restaurant.infrastructure.util.Toolkit;
import restaurant.manager.Manager;
import restaurant.payment.model.CashPayment;
import restaurant.payment.model.CashRegister;

/**
 * Comprehensive integration and unit tests for the Restaurant simulation.
 */
public class RestaurantTest {
	/**
	 * Test a full simulation of the restaurant, running without any exceptions.
	 */
	@Test
	void testSimulationRunsWithoutException() {
		Manager manager = new Manager("TestManager", 2);

		assertDoesNotThrow(() -> {
			manager.simulateRestaurantDay(2);
			Thread.sleep(3000);
			manager.closeRestaurant();
		}, "Simulation should run without throwing exceptions.");
	}

	/**
	 * Demonstrates handling multiple parallel preparations with CompletableFuture.
	 * Serves as a timing showcase — not part of automated checks.
	 */
	@Test
	public void testMultiplePreparations() throws ExecutionException, InterruptedException {
		CompletableFuture<String> prep1 = CompletableFuture.supplyAsync(() -> {
			sleep(800);
			return "Pizza ready";
		});

		CompletableFuture<String> prep2 = CompletableFuture.supplyAsync(() -> {
			sleep(1200);
			return "Salad ready";
		});

		CompletableFuture.allOf(prep1, prep2).join();

		List<String> results = List.of(prep1.join(), prep2.join());
		assertTrue(results.contains("Pizza ready"));
		assertTrue(results.contains("Salad ready"));

		System.out.println("End:   " + Toolkit.now.get());
		System.out.println("Results: " + results);
	}

	/**
	 * Ensures an order sent directly to the kitchen is eventually marked as
	 * PREPARED.
	 */
	@Test
	void testOrderWithFuture() throws Exception {
		Kitchen kitchen = new Kitchen(1);
		Order order = Toolkit.testOrder.get();

		CompletableFuture<Order> future = kitchen.acceptOrder(order);
		Order finished = future.get(30, TimeUnit.SECONDS);

		assertEquals(OrderStatus.PREPARED, finished.getStatus());
		kitchen.close();
		assertTrue(kitchen.isClosed(), "Kitchen should be terminated after close()");
	}

	/**
	 * Verifies successful cash payment updates order status to PAID.
	 */
	@Test
	void testSuccessfulCashPayment() {
		Customer customer = Toolkit.testCustomer.get();
		Order order = Toolkit.testOrderWithCustomer.apply(customer);

		CashRegister register = new CashRegister();
		boolean success = register.pay(order, new CashPayment(order.getTotalPrice()));

		assertTrue(success);
		assertEquals(OrderStatus.PAID, order.getStatus());
	}

	/**
	 * Ensures total price calculation sums up all dishes correctly.
	 */
	@Test
	void testTotalPriceCalculation() {
		Customer customer = new Customer("Alice", 1);
		Dish d1 = new Dish("Pizza", Category.MAIN_COURSE, 10.0);
		Dish d2 = new Dish("Salad", Category.STARTER, 5.0);
		Order order = Order.create(customer, List.of(d1, d2));

		assertEquals(15.0, order.getTotalPrice(), 0.01);
	}

	/**
	 * Tests that placing an order automatically stores it inside the Customer.
	 */
	@Test
	void testPlaceOrderStoresOrderInCustomer() {
		Customer customer = new Customer("Alice", 1);
		Order order = Order.create(customer, List.of(new Dish("Soup", Category.STARTER, 4.0)));
		assertEquals(order, customer.getOrder());
	}

	/**
	 * Tests that paying without any order does not crash or throw exceptions.
	 */
	@Test
	void testPayWithoutOrder() {
		Customer customer = new Customer("Bob", 2);
		assertNull(customer.getOrder());
		assertDoesNotThrow(() -> customer.pay(new Waiter(new Kitchen(1), new CashRegister()), new CashPayment(10)));
	}

	/**
	 * Simulates a successful payment flow via Waiter.
	 */
	@Test
	void testSuccessfulPaymentFlow() throws Exception {
		Kitchen kitchen = new Kitchen(1);
		Waiter waiter = new Waiter(kitchen, new CashRegister());
		Customer customer = Toolkit.testCustomer.get();
		Order order = Toolkit.testOrderWithCustomer.apply(customer);

		CompletableFuture<Order> future = customer.placeOrder(waiter, order);
		order.setStatus(OrderStatus.PREPARED);
		customer.pay(waiter, new CashPayment(order.getTotalPrice()));

		assertEquals(OrderStatus.PAID, order.getStatus());
		kitchen.close();
	}

	/**
	 * Validates Menu grouping by category.
	 */
	@Test
	void testByCategory() {
		Dish d1 = new Dish("Pizza", Category.MAIN_COURSE, 8.0);
		Dish d2 = new Dish("Soup", Category.STARTER, 4.0);
		Menu menu = new Menu(List.of(d1, d2));

		Map<Category, List<Dish>> grouped = menu.byCategory();

		assertTrue(grouped.containsKey(Category.MAIN_COURSE));
		assertTrue(grouped.containsKey(Category.STARTER));
		assertEquals(List.of(d2), grouped.get(Category.STARTER), "Should be sorted alphabetically");
	}

	/**
	 * Validates partitioning by price limit.
	 */
	@Test
	void testByPrice() {
		Dish d1 = new Dish("Coffee", Category.DRINK, 2.5);
		Dish d2 = new Dish("Steak", Category.MAIN_COURSE, 20.0);
		Menu menu = new Menu(List.of(d1, d2));

		Map<Boolean, List<Dish>> partitioned = menu.byPrice(10.0);

		assertTrue(partitioned.get(true).contains(d1));
		assertTrue(partitioned.get(false).contains(d2));
	}

	/**
	 * Ensures Preparation returns the same order instance.
	 */
	@Test
	void testPreparationReturnsSameOrder() throws Exception {
		Customer customer = new Customer("Dave", 5);
		Order order = Order.create(customer, List.of(new Dish("Burger", Category.MAIN_COURSE, 7.5)));

		Preparation prep = new Preparation(order, 100);
		Order result = prep.call();

		assertEquals(order, result);
	}

	/**
	 * Runs the demo flow to verify Restaurant.start() executes correctly.
	 */
	@Test
	void testDemoCustomerFlow() {
		Restaurant restaurant = new Restaurant(1);
		restaurant.start(); // demo: creates one order and processes it fully

		List<Customer> customers = restaurant.getCustomers();
		assertFalse(customers.isEmpty(), "At least one demo customer should exist.");

		Order order = customers.get(0).getOrder();
		assertNotNull(order, "Customer should have an order assigned.");
		assertEquals("Peter", customers.get(0).getName());
	}

	/** Helper: sleep without checked exception clutter */
	private static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}

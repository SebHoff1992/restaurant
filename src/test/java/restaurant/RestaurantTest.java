package restaurant;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import restaurant.model.Category;
import restaurant.model.Customer;
import restaurant.model.Dish;
import restaurant.model.Menu;
import restaurant.model.Order;
import restaurant.model.OrderStatus;
import restaurant.model.Preparation;
import restaurant.payment.intern.CashPayment;
import restaurant.payment.intern.CashRegister;
import restaurant.service.Kitchen;
import restaurant.service.Waiter;
import restaurant.util.Toolkit;

/**
 * Integration tests for the restaurant simulation.
 */
public class RestaurantTest {

	/**
	 * Demonstrates handling multiple parallel preparations with CompletableFuture.
	 * Disabled because it's only a timing showcase and not part of automated tests.
	 */
	@Test
	public void testMultiplePreparations() throws ExecutionException, InterruptedException {
		System.out.println("Start: " + Toolkit.now.get());

		// Preparation 1
		CompletableFuture<String> prep1 = CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(800); // takes 0.8s
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			return "Pizza";
		}).thenApply(s -> s + " ready");

		// Preparation 2
		CompletableFuture<String> prep2 = CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(1200); // takes 1.2s
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			return "Salad";
		}).thenApply(s -> s + " ready");

		// Collect futures
		List<CompletableFuture<String>> all = List.of(prep1, prep2);

		// Wait until all are finished
		CompletableFuture<Void> allDone = CompletableFuture.allOf(all.toArray(new CompletableFuture[0]));
		allDone.join();

		// Gather results
		List<String> results = all.stream().map(CompletableFuture::join).toList();

		// Assertions
		assertTrue(results.contains("Pizza ready"));
		assertTrue(results.contains("Salad ready"));

		System.out.println("End:   " + Toolkit.now.get());
		System.out.println("Results: " + results);
	}

	/**
	 * Verifies that an order sent directly to the kitchen is eventually marked as
	 * PREPARED and the kitchen can be closed cleanly.
	 */
	@Test
	void testOrderWithFuture() throws Exception {
		Kitchen kitchen = new Kitchen(1);

		Order order = Toolkit.testOrder.get();
		CompletableFuture<Order> future = kitchen.acceptOrder(order);

		// Wait for order to be processed
		Order finished = future.get(30, TimeUnit.SECONDS);

		assertEquals(OrderStatus.PREPARED, finished.getStatus());

		// Close kitchen
		kitchen.close();

		// Verify that the kitchen is really terminated
		assertTrue(kitchen.isClosed(), "Kitchen should be terminated after close()");
	}

	@Test
	void testSuccessfulCashPayment() {
		Customer c = new Customer("Charlie", 1);
		Order order = Order.create(c, List.of(new Dish("Burger", Category.MAIN_COURSE, 8.0)));

		CashRegister register = new CashRegister();
		boolean success = register.pay(order, new CashPayment(order.getTotalPrice()));

		assertTrue(success);
		assertEquals(OrderStatus.PAID, order.getStatus());
	}

	@Test
	void testTotalPriceCalculation() {
		Customer customer = new Customer("Alice", 1);
		Dish d1 = new Dish("Pizza", Category.MAIN_COURSE, 10.0);
		Dish d2 = new Dish("Salad", Category.STARTER, 5.0);

		Order order = Order.create(customer, List.of(d1, d2));

		assertEquals(15.0, order.getTotalPrice(), 0.01);
	}

	@Test
	void testPlaceOrderStoresOrderInCustomer() {
		Customer c = new Customer("Alice", 1);
		Order o = Order.create(c, List.of(new Dish("Soup", Category.STARTER, 4.0)));

		assertEquals(o, c.getOrder());
	}

	@Test
	void testPayWithoutOrder() {
		Customer c = new Customer("Bob", 2);
		assertNull(c.getOrder());
		// Just ensure it doesn't throw an exception
		assertDoesNotThrow(() -> c.pay(new Waiter(new Kitchen(1), new CashRegister()), new CashPayment(10)));
	}

	@Test
	void testSuccessfulPaymentFlow() throws Exception {
		Kitchen kitchen = new Kitchen(1);
		CashRegister register = new CashRegister();
		Waiter waiter = new Waiter(kitchen, register);

		Customer c = new Customer("Charlie", 3);
		Order o = Order.create(c, List.of(new Dish("Pizza", Category.MAIN_COURSE, 8.0)));

		CompletableFuture<Order> f = c.placeOrder(waiter, o);
		o.setStatus(OrderStatus.PREPARED);
		c.pay(waiter, new CashPayment(o.getTotalPrice()));

		assertEquals(OrderStatus.PAID, o.getStatus());
		kitchen.close();
	}

	@Test
	void testByCategory() {
		Dish d1 = new Dish("Pizza", Category.MAIN_COURSE, 8.0);
		Dish d2 = new Dish("Soup", Category.STARTER, 4.0);
		Menu menu = new Menu(List.of(d1, d2));

		Map<Category, List<Dish>> grouped = menu.byCategory();

		assertTrue(grouped.containsKey(Category.MAIN_COURSE));
		assertTrue(grouped.containsKey(Category.STARTER));
		assertEquals(List.of(d2), grouped.get(Category.STARTER)); // sorted by name
	}

	@Test
	void testByPrice() {
		Dish d1 = new Dish("Coffee", Category.DRINK, 2.5);
		Dish d2 = new Dish("Steak", Category.MAIN_COURSE, 20.0);
		Menu menu = new Menu(List.of(d1, d2));

		Map<Boolean, List<Dish>> partitioned = menu.byPrice(10.0);

		assertTrue(partitioned.get(true).contains(d1));
		assertTrue(partitioned.get(false).contains(d2));
	}

	@Test
	void testPreparationReturnsSameOrder() throws Exception {
		Customer c = new Customer("Dave", 5);
		Order o = Order.create(c, List.of(new Dish("Burger", Category.MAIN_COURSE, 7.5)));

		Preparation prep = new Preparation(o, 100);
		Order result = prep.call();

		assertEquals(o, result);
	}

	@Test
	void testDemoCustomerFlow() {
		Restaurant r = new Restaurant(1);
		r.start(); // runs with demo customer + order

		List<Customer> customers = r.getCustomers();
		assertFalse(customers.isEmpty());

		Order order = customers.get(0).getOrder();
		assertNotNull(order);
		assertEquals("Peter", customers.get(0).getName());
	}

}

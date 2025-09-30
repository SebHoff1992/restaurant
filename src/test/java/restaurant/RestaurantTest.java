package restaurant;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import restaurant.model.OrderStatus;
import restaurant.model.Order;
import restaurant.service.Kitchen;
import restaurant.util.Toolkit;

/**
 * Integration tests for the restaurant simulation.
 */
public class RestaurantTest {

	/**
	 * Demonstrates handling multiple parallel preparations with CompletableFuture.
	 * Disabled because it's only a timing showcase and not part of automated tests.
	 */
	@Disabled
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
}

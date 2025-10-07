package restaurant;

import java.util.ArrayList;
import java.util.List;
import restaurant.model.Customer;
import restaurant.model.Order;
import restaurant.model.Dish;
import restaurant.model.Category;

/**
 * Utility to simulate a memory leak by keeping all customers in a static list
 * that is never cleared.
 */
public class LeakSimulator {

	// Static list holds references forever (memory leak)
	private static final List<Customer> leakedCustomers = new ArrayList<>();

	public static void simulateLeak(int numberOfCustomers) {
		for (int i = 0; i < numberOfCustomers; i++) {
			Customer c = new Customer("Leaky-" + i, i);
			Order o = Order.create(c, List.of(new Dish("Pizza", Category.MAIN_COURSE, 8.5)));
			leakedCustomers.add(c); // never removed → leak grows
		}
		System.out.println("Simulated leak with " + numberOfCustomers + " customers.");
	}

	public static void main(String[] args) {
		while (true) {
			LeakSimulator.simulateLeak(Integer.MAX_VALUE);
			try {
				Thread.sleep(500); // half a second
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}

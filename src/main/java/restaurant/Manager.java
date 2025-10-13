package restaurant;

import restaurant.core.Restaurant;
import restaurant.util.Toolkit;

public class Manager {

	private String name;
	private final Restaurant restaurant;

	public Manager(String name, int numChefs) {
		this.name = name;
		this.restaurant = new Restaurant(numChefs);
	}

	/** Closes the restaurant at the end of the day */
	public void closeRestaurant() {
		Toolkit.logTime.accept(name + " wants to close the restaurant...");
		restaurant.finishDay();
		Toolkit.logTime.accept("Restaurant successfully closed by " + name + ".");
	}

	public String getName() {
		return name;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	/**
	 * Simulates a full restaurant day: customers enter, order, pay, and leave.
	 */
	public void simulateRestaurantDay(int numCustomers) {
		Toolkit.logTime.accept(name + " starts the restaurant day simulation!");

		// Manager triggers the restaurant operations
		restaurant.simulateCustomerEnters(numCustomers);
		restaurant.simulateOrders();// pays automatically after order received
//		restaurant.simulatePayments();
		restaurant.simulateAllCustomersExit();
	}

	/**
	 * Entry point for simulation.
	 */
	public static void main(String[] args) {
		Manager manager = new Manager("Sebastian", 1);
		manager.simulateRestaurantDay(3);
		manager.closeRestaurant();
	}
}

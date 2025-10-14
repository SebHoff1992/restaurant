package restaurant.simulation.service.impl;

import org.springframework.stereotype.Service;

import restaurant.infrastructure.util.Toolkit;
import restaurant.simulation.Restaurant;
import restaurant.simulation.service.ManagerService;

/**
 * Handles simulation logic and orchestrates restaurant flow.
 */
@Service
public class ManagerServiceImpl implements ManagerService {

	private Restaurant restaurant;
	private String name = "Sebastian"; // fixed for now;
	private int numChefs = 3;

	/**
	 * Simulates a full restaurant day: customers enter, order, pay, and leave.
	 */
	@Override
	public void simulateRestaurantDay(int numCustomers) {
		Toolkit.logTime.accept(name + " starts the restaurant day simulation!");
		this.restaurant = new Restaurant(numChefs);
		// Manager triggers the restaurant operations
		restaurant.simulateCustomerEnters(numCustomers);
		restaurant.simulateOrders();// pays automatically after order received
//		restaurant.simulatePayments();
		restaurant.simulateAllCustomersExit();
	}

	/** Closes the restaurant at the end of the day */
	@Override
	public void closeRestaurant() {
		if (restaurant == null) {
			Toolkit.logTime.accept("No active restaurant to close!");
			return;
		}
		Toolkit.logTime.accept(name + " wants to close the restaurant...");
		restaurant.close();
		Toolkit.logTime.accept("Restaurant successfully closed by " + name + ".");
	}

	@Override
	public String getReport() {
		if (restaurant == null) {
			return "No report available — simulation has not been run yet.";
		}
		return restaurant.getReport();
	}

}

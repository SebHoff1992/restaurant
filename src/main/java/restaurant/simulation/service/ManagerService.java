package restaurant.simulation.service;

public interface ManagerService {
	void simulateRestaurantDay(int numCustomers);

	void closeRestaurant();

	String getReport();

}
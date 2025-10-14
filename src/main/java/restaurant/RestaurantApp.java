package restaurant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import restaurant.simulation.model.Manager;

/**
 * Main Spring Boot application entry point for the Restaurant system.
 *
 * <p>
 * This class boots the entire Spring context, including controllers, services,
 * repositories, and configuration beans. It replaces the standalone main method
 * that previously ran the simulation manually.
 * </p>
 *
 * <p>
 * When the application starts, Spring scans the 'restaurant' package and all
 * subpackages for components annotated with {@code @Component},
 * {@code @Service}, {@code @Repository}, and {@code @Controller}.
 * </p>
 *
 * <p>
 * The simulation logic (Manager, Restaurant, Kitchen, etc.) can be triggered
 * automatically using {@code CommandLineRunner} or via REST endpoints.
 * </p>
 */
@SpringBootApplication
public class RestaurantApp {

	@Value("${app.simulation.enabled:true}")
	private boolean simulationEnabled;

	/**
	 * Launches the Spring Boot application.
	 *
	 * @param args program arguments (optional)
	 */
	public static void main(String[] args) {
		SpringApplication.run(RestaurantApp.class, args);
	}

	/**
	 * Runs the restaurant simulation after the Spring context has been initialized.
	 *
	 * @return a {@link CommandLineRunner} that starts the simulation if enabled
	 */
	@Bean
	CommandLineRunner runSimulation() {
		return args -> {
			if (!simulationEnabled) {
				System.out.println("🚫 Simulation is disabled via application.properties");
				return;
			}

			System.out.println("Starting restaurant simulation...");
			Manager manager = new Manager("Sebastian", 3);
			manager.simulateRestaurantDay(2);
			manager.closeRestaurant();
			System.out.println("Simulation finished successfully!");
		};
	}
}

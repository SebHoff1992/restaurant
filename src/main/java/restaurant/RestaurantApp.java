package restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application entry point for the Restaurant system.
 *
 * <p>This class boots the entire Spring context, including controllers,
 * services, repositories, and configuration beans. It replaces the
 * standalone main method that previously ran the simulation manually.</p>
 *
 * <p>When the application starts, Spring scans the 'restaurant' package and
 * all subpackages for components annotated with {@code @Component},
 * {@code @Service}, {@code @Repository}, and {@code @Controller}.</p>
 *
 * <p>The simulation logic (Manager, Restaurant, Kitchen, etc.) can be triggered
 * automatically using {@code CommandLineRunner} or via REST endpoints.</p>
 */
@SpringBootApplication
public class RestaurantApp {

    /**
     * Launches the Spring Boot application.
     *
     * @param args program arguments (optional)
     */
    public static void main(String[] args) {
        SpringApplication.run(RestaurantApp.class, args);
    }
}

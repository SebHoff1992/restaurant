package restaurant.core.model;

import static java.util.stream.Collectors.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Menu manages all dishes and provides helper methods to group them by
 * category, price, etc.
 */
public class Menu {

	private final List<Dish> dishes;

	/**
	 * Create a new menu with the given list of dishes.
	 * 
	 * @param dishes the dishes available in this menu
	 */
	public Menu(List<Dish> dishes) {
		this.dishes = dishes;
	}

	/**
	 * @return all dishes in the menu
	 */
	public List<Dish> getAllDishes() {
		return dishes;
	}

	/**
	 * Groups dishes by category, with categories sorted in a TreeMap and dishes
	 * sorted by name inside each category.
	 * 
	 * @return a map of categories to lists of dishes
	 */
	public Map<Category, List<Dish>> byCategory() {
		return dishes.stream().collect(groupingBy(Dish::category, () -> new TreeMap<>(),
				collectingAndThen(toList(), list -> list.stream().sorted(Comparator.comparing(Dish::name)).toList())));
	}

	/**
	 * Splits dishes into two groups based on a price threshold.
	 * 
	 * @param threshold the maximum price for the first group
	 * @return a partitioned map: true = cheaper/equal to threshold, false = more
	 *         expensive
	 */
	public Map<Boolean, List<Dish>> byPrice(double threshold) {
		return dishes.stream().collect(partitioningBy(d -> d.price() <= threshold));
	}

	@Override
	public String toString() {
		return dishes.stream().map(d -> String.format("%s (%.2f €)", d.name(), d.price()))
				.collect(joining(", ", "[Menu: ", "]"));
	}
}

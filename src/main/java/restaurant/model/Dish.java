package restaurant.model;

import java.util.Objects;

/**
 * Represents a dish on the menu with a name, category, and price.
 * This class is a record and provides built-in immutability.
 */
public record Dish(String name, Category category, double price) {

    /**
     * Compact constructor with validation logic.
     * Ensures that the category is not null and the price is greater than zero.
     */
    public Dish {
        Objects.requireNonNull(category, "Category must not be null.");
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0.");
        }
    }

    @Override
    public String toString() {
        return name + " (" + category + ") - " + price + " €";
    }
}

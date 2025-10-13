package restaurant;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import restaurant.core.customer.model.Customer;
import restaurant.infrastructure.util.Toolkit;

/**
 * Tests for table allocation logic in Toolkit.
 */
public class ToolkitTableTest {

	@Test
	void testCustomerGetsFreeTable() {
		List<Integer> freeTables = IntStream.rangeClosed(1, 3).boxed().collect(Collectors.toList());

		Customer c = Toolkit.createCustomerForFreeTable.apply(freeTables);
		assertNotNull(c, "Customer should be created if tables are available.");
		assertFalse(freeTables.contains(c.getTableNumber()), "Assigned table must be removed from free table list.");
	}

	@Test
	void testNoCustomerIfNoTablesAvailable() {
		List<Integer> freeTables = java.util.List.of();
		Customer c = Toolkit.createCustomerForFreeTable.apply(freeTables);
		assertNull(c, "No customer should be created if there are no free tables.");
	}
}

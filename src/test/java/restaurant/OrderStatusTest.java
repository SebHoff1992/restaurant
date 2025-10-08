package restaurant;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import restaurant.model.OrderStatus;

/**
 * Unit tests for the OrderStatus enum logic.
 */
public class OrderStatusTest {

	@Test
	void testCanBePaidLogic() {
		assertFalse(OrderStatus.OPEN.canBePaid(), "OPEN orders can not be paid.");
		assertFalse(OrderStatus.IN_PREPARATION.canBePaid(), "IN_PREPARATION orders should not allow payment.");
		assertTrue(OrderStatus.PREPARED.canBePaid(), "PREPARED orders can be paid.");
//		assertFalse(OrderStatus.PAYMENT_FAILED.canBePaid(), "Failed payments cannot be retried here.");
		assertFalse(OrderStatus.PAID.canBePaid(), "PAID orders should not allow further payment.");
	}

	@Test
	void testIsFinalizedLogic() {
		assertFalse(OrderStatus.OPEN.isFinalized());
		assertFalse(OrderStatus.PREPARED.isFinalized());
		assertTrue(OrderStatus.PAID.isFinalized(), "Only PAID orders are considered finalized.");
	}
}

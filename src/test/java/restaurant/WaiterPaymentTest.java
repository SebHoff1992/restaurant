package restaurant;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import restaurant.model.Category;
import restaurant.model.Customer;
import restaurant.model.Dish;
import restaurant.model.Order;
import restaurant.model.OrderStatus;
import restaurant.payment.intern.CashPayment;
import restaurant.payment.intern.CashRegister;
import restaurant.service.Kitchen;
import restaurant.service.Waiter;
import restaurant.util.Toolkit;

/**
 * Tests that the Waiter only accepts valid payments.
 */
public class WaiterPaymentTest {

	@Test
	void testWaiterRejectsPaymentBeforePreparation() {
		CashRegister cashRegister = new CashRegister();
		Waiter waiter = new Waiter(null, cashRegister);

		Customer customer = Toolkit.testCustomer.get();
		Order order = Toolkit.testOrderWithCustomer.apply(customer);

		// Manually ensure order is OPEN
		order.setStatus(OrderStatus.OPEN);

		waiter.processPayment(customer, order, new CashPayment(order.getTotalPrice()));

		assertEquals(OrderStatus.OPEN, order.getStatus(),
				"Payment should not change status if order is not yet prepared.");
	}

	@Test
	void testWaiterAcceptsPaymentAfterPrepared() {
		CashRegister cashRegister = new CashRegister();
		Waiter waiter = new Waiter(null, cashRegister);

		Customer customer = new Customer("TestCustomer", 2);
		Order order = Order.create(customer, java.util.List.of(new Dish("Pasta", Category.MAIN_COURSE, 12.0)));

		order.setStatus(OrderStatus.PREPARED);
		waiter.processPayment(customer, order, new CashPayment(order.getTotalPrice()));

		assertEquals(OrderStatus.PAID, order.getStatus(), "Order should be marked as PAID after successful payment.");
	}
}

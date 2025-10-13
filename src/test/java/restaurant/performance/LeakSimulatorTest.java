package restaurant.performance;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import restaurant.infrastructure.monitoring.LeakSimulator;

/**
 * This test does not assert anything – it only creates load so that VisualVM
 * can show memory usage growing.
 */
public class LeakSimulatorTest {

	@Test
	@Disabled
	void simulateMemoryLeak() {

		// Sleep so VisualVM has time to attach
		try {
			Thread.sleep(10_000); // 10 seconds
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		// Add 10,000 customers to the static list
		LeakSimulator.simulateLeak(10_000);
	}
}

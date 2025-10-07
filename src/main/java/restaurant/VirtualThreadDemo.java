package restaurant;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

/**
 * Demonstrates how lightweight Virtual Threads scale compared to traditional
 * threads.
 */
public class VirtualThreadDemo {

	public static void main(String[] args) {
		System.out.println("Starting Virtual Thread demo...");
		int number = 1;
		Callable<Integer> task = () -> {
			Thread.sleep(10_000);
			System.out.println("Virtual thread " + number + "finished.");
			return number;
		};

		try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
			List<Future<Integer>> futures = IntStream.range(0, 10_000).mapToObj(i -> executor.submit(task)).toList();
			// Launch 10,000 virtual threads
			IntStream.range(0, 10_000).forEach(i -> executor.submit(() -> {
				Thread.sleep(1000);
				if (i % 1000 == 0) {
					System.out.println("Virtual thread " + i + " finished.");
				}
				return i;
			}));

			// Executor will automatically wait for all threads to finish (AutoCloseable)
		}

		System.out.println("All virtual threads completed!");

	}
}

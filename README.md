# Restaurant Simulation

A multithreaded Java simulation of a restaurant, built to demonstrate **advanced Java features**, **concurrency**, and **clean code practices**.  
This project serves as a portfolio example to showcase my coding style, problem-solving approach, and use of modern Java.  

---

## Features
- Modular design (`model`, `service`, `payment`, `util`)  
- Concurrency with multiple cooks (threads) and an order queue (BlockingQueue)  
- Reporting with modern String API methods (Java 11+)  
- Unit testing with JUnit 5  
- Exploration of advanced Java language features  

---

## Code Example
```java
// Example: printing a daily report using CompletableFutures
Restaurant restaurant = new Restaurant();
restaurant.start();
restaurant.printReport();
```

---

## Advanced Java Features

### Generics
- Wildcards (`?`), upper/lower bounds (`extends`, `super`)  
- PECS principle (*Producer Extends, Consumer Super*)  

### Streams
- **Intermediate operations**: `map`, `filter`, `flatMap`, `distinct`, `sorted`  
- **Terminal operations**: `forEach`, `collect`, `reduce`, `count`  
- **Optional results**: `findFirst`, `findAny`, `max`, `min`  
- **Parallel streams**: parallel processing for performance, trade-offs with overhead  

### Functional Interfaces
- **Core**:
  - `Consumer<T>` → performs an action, no return  
  - `Supplier<T>` → lazy object creation  
  - `Predicate<T>` → boolean test  
  - `Function<T,R>` → maps input to output  
  - `BiConsumer<T,U>` → action with two inputs (e.g., `(Order, Message)` logging)  
- **Custom**:
  - `TriConsumer<T,U,V>` → extended interface with three arguments, implemented in Toolkit  
- **Method References**:
  - Concise syntax for lambdas, e.g. `System.out::println`  

### Java Module System (since Java 9)
- `module-info.java` used to control visibility of packages  
- Demonstrates encapsulation and explicit dependencies  

---

## Concurrency & Multithreading

### Thread Types
- Difference between `Thread`, `Runnable`, `Callable`, `ExecutorService`  
- Executors used to manage worker threads (cooks)  

### Task Processing
- **BlockingQueue**: thread-safe order queue between waiters and cooks  
- **Callable & Future**: asynchronous tasks with return values (e.g., preparation duration)  
- **CompletableFuture**: async composition and reporting  
- **Parallel Streams**: declarative parallelism, when suitable  

---

## Synchronization & Thread Safety
- Compared **`synchronized` vs. atomic classes**: trade-off between safety and throughput  
- Evaluated `Collections.synchronizedList` and `AtomicInteger`  
- Replaced with design choices avoiding shared mutable state where possible  
- Key insight: **better to design for immutability and handover (BlockingQueue)** than rely on synchronization  

---

## Lessons Learned
- Explored `AtomicInteger` and `Collections.synchronizedList` but replaced with simpler, cleaner solutions  
- Recognized that `synchronized` ensures correctness but can hurt performance under contention  
- Prefer designing thread-safe handover (queues) instead of manual locking  

---

## Best Practices & Organization
- Static final constants → `UPPER_CASE`  
- Functional fields → `camelCase`  
- Toolkit class for reusable `Consumer`, `Predicate`, `TriConsumer` utilities  
- `import static` for concise access to functional helpers  
- Modular organization with clear separation of concerns  

---

## Project Structure
```
restaurant/
 ├── src/
 │   ├── main/
 │   │   ├── java/          # Application source code
 │   │   └── resources/     # Application resources
 │   └── test/
 │       ├── java/          # Unit tests
 │       └── resources/     # Test resources
 ├── pom.xml                # Maven project descriptor
 └── README.md              # Project description (this file)
```

---

## Build & Run

### Prerequisites
- Java 21+
- Maven 3.9+

### Build & Test
```bash
mvn clean install
mvn test
```

### Run Application
```
src/main/java/restaurant/Restaurant.java
```

---

## Author
Developed by **Sebastian Hoffmann**  
📌 GitHub: [SebHoff1992](https://github.com/SebHoff1992)  

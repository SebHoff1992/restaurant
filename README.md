# 🍽️ Restaurant Simulation (Java 21)

A **multithreaded and modular Java simulation** of a restaurant, demonstrating modern Java features such as  
**Virtual Threads (Project Loom)**, **CompletableFutures**, **functional programming**, and **clean architecture principles**.

---

## 🧩 Overview

This project simulates a full restaurant workflow:
- Customers enter asynchronously (using Virtual Threads)
- Orders are created, prepared, and paid
- Waiters coordinate with the kitchen and cash register
- Each table is managed individually and freed when customers leave
- A manager oversees the entire process (opening and closing)

The simulation showcases:
- Concurrency control and synchronization
- Functional programming (Supplier, Function, Predicate, BiConsumer)
- Modular architecture and clean design
- Practical performance concepts from modern JVM

---

## ⚙️ Architecture

### 🧱 Core Components
| Class | Responsibility |
|--------|----------------|
| **Manager** | Opens and closes the restaurant; triggers simulation |
| **Restaurant** | Coordinates kitchen, waiter, and customers |
| **Waiter** | Handles order processing and payment validation |
| **Kitchen** | Manages concurrent food preparation via worker threads |
| **CashRegister** | Processes payments and ensures correct order state |
| **Toolkit** | Provides reusable functional utilities for logging, timing, and test data |

---

## 🧵 Concurrency with Virtual Threads

The simulation uses **`Executors.newVirtualThreadPerTaskExecutor()`** (Java 21)  
to create lightweight concurrent tasks for each customer.

Each customer runs independently:

✅ This allows **hundreds of concurrent customers** without performance overhead,  
unlike traditional platform threads.

---

## 🪑 Table Management

To simulate realistic seating:
- The restaurant maintains a `List<Integer> freeTables`.
- Each customer is assigned a unique, available table.
- Tables are freed once a customer leaves.
- Access is **thread-safe** via `synchronized (freeTables)` to prevent race conditions.

Example:
```java
Customer c = Toolkit.createCustomerForFreeTable.apply(freeTables);
if (c == null) {
    Toolkit.logTime.accept("No free tables available — customer leaves.");
    return;
}
customers.add(c);
Toolkit.logTime.accept(c.getName() + " sits at table " + c.getTableNumber());
```

---

## 💰 Payment Flow & Order States

Each `Order` passes through multiple logical states:

```java
OPEN → IN_PREPARATION → PREPARED → SERVED → PAID
```

The **Waiter** now ensures:
- Payments can only occur when the order is **PREPARED or SERVED**.
- Already paid or finalized orders are ignored.

Defined in `OrderStatus`:
```java
public boolean isFinalized() { return this == PAID; }
```

This rule is enforced in `Waiter.processPayment()`:
```java
if (order.getStatus().isFinalized()) return;

cashRegister.pay(order, payment);
```

---

## 🧰 Functional Toolkit

The `Toolkit` class centralizes reusable functional utilities:

| Type | Example | Purpose |
|------|----------|----------|
| `Supplier<T>` | `testOrder.get()` | Generates random orders |
| `Function<T,R>` | `createCustomerForFreeTable.apply(freeTables)` | Creates customer with available table |
| `Predicate<T>` | `isValidOrder` | Validates active orders |
| `BiConsumer<T,U>` | `logger.accept(order, message)` | Logs with contextual data |

This keeps the codebase **functional, modular, and concise**.

---

## 🧠 Performance & JVM Insights

The project is also designed to experiment with JVM performance tools:
- **Garbage Collection** (G1, ZGC)
- **JIT Compilation** (`-XX:+PrintCompilation`)
- **Memory profiling** with `jvisualvm`, `jmap`, and heap dumps
- **Leak Simulation** (`LeakSimulator.java`) for heap analysis

Example heap dump creation:
```bash
jmap -dump:format=b,file=heap.hprof <pid>
```

---

## 🧩 Module System

The project uses the Java Module System (`module-info.java`) to export packages:
```java
module restaurant {
    exports restaurant;
    exports restaurant.model;
    exports restaurant.service;
    exports restaurant.payment;
    exports restaurant.util;
}
```

This ensures clean dependency boundaries and prevents *split-package* issues.

---

## 🧪 Unit Testing

JUnit 5 (`junit-jupiter`) is integrated for testing:
- `RestaurantTest` End-to-end test for order lifecycle, price calculation, and customer flow using Virtual Threads
- `OrderStatusTest` Validates allowed transitions and finalization logic in the order state machine
- `LeakSimulatorTest` demonstrates heap growth and GC activity
- `WaiterPaymentTest` Ensures the waiter correctly validates and processes payments
- `ToolkitTableTest` Confirms thread-safe table assignment using functional Supplier and Function utilities
- ``


Run tests with:
```bash
mvn test
```

---

## 🧠 Key Learning Topics (for Alignerr Assessment)

| Area | Concepts Covered |
|------|------------------|
| **Performance Optimization** | GC tuning, JIT, heap analysis |
| **Software Architecture** | SRP, modular design, dependency boundaries |
| **Advanced Java Features** | Generics, Streams, Functional Interfaces |
| **Concurrency** | Threads, Executors, Virtual Threads |
| **Functional Programming** | Supplier, Predicate, Function, BiConsumer |
| **Module System** | `exports`, `requires`, avoiding split packages |

---

## 📘 Requirements

- **Java 21+**
- **Maven 3.9+**
- Optional: VisualVM, JConsole, JDK tools for profiling

---

## 🧾 License
MIT License © 2025 – Created as part of Java training and performance optimization study.

---

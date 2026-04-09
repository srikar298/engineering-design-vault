import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * ============================================================================
 * ⚡ LAMBDAS & STREAMS MASTERY: Declarative Data Processing
 * ============================================================================
 */
public class LambdaStreamsMastery {

    // --- Domain model ---
    enum Status { PENDING, CONFIRMED, SHIPPED, DELIVERED }

    static class Order {
        private final String id;
        private final String customer;
        private final double amount;
        private final Status status;

        public Order(String id, String customer, double amount, Status status) {
            this.id = id; this.customer = customer;
            this.amount = amount; this.status = status;
        }
        public String getId()       { return id; }
        public String getCustomer() { return customer; }
        public double getAmount()   { return amount; }
        public Status getStatus()   { return status; }
        @Override public String toString() {
            return String.format("Order{id=%s, customer=%s, $%.2f, %s}", id, customer, amount, status);
        }
    }

    public static void main(String[] args) {
        List<Order> orders = Arrays.asList(
            new Order("ORD-1", "Alice",  999.99, Status.DELIVERED),
            new Order("ORD-2", "Bob",    49.99,  Status.PENDING),
            new Order("ORD-3", "Alice",  1500.0, Status.SHIPPED),
            new Order("ORD-4", "Charlie",250.0,  Status.DELIVERED),
            new Order("ORD-5", "Bob",    89.99,  Status.CONFIRMED)
        );

        // ── 1. THE 4 CORE FUNCTIONAL INTERFACES ──────────────────────────────
        System.out.println("=== 1. Core Functional Interfaces ===");

        Predicate<Order>        isDelivered   = o -> o.getStatus() == Status.DELIVERED;
        Function<Order, String> toSummary     = o -> o.getId() + " → " + o.getCustomer();
        Consumer<Order>         printOrder    = o -> System.out.println("  [LOG] " + o);
        Supplier<List<Order>>   freshList     = ArrayList::new;

        System.out.println("Predicate test (ORD-1 delivered?): " + isDelivered.test(orders.get(0)));
        System.out.println("Function map (ORD-2 summary):      " + toSummary.apply(orders.get(1)));
        System.out.println("Consumer side-effect:");
        printOrder.accept(orders.get(2));
        System.out.println("Supplier creates: " + freshList.get().getClass().getSimpleName());

        // ── 2. METHOD REFERENCES ─────────────────────────────────────────────
        System.out.println("\n=== 2. Method References ===");
        orders.stream()
              .map(Order::getCustomer)   // Instance method reference: o -> o.getCustomer()
              .distinct()
              .forEach(System.out::println); // Instance method on instance

        // ── 3. STREAM PIPELINE ───────────────────────────────────────────────
        System.out.println("\n=== 3. Multi-stage Stream Pipeline ===");

        // Business question: "What is the total revenue from DELIVERED orders over $500?"
        double highValueRevenue = orders.stream()
            .filter(isDelivered)                       // Keep DELIVERED only
            .filter(o -> o.getAmount() > 500)          // Keep high-value only
            .mapToDouble(Order::getAmount)             // Project to primitive double
            .sum();                                    // Terminal: aggregate
        System.out.printf("High-value DELIVERED revenue: $%.2f%n", highValueRevenue);

        // Business question: "Group all orders by customer."
        System.out.println("\n=== 4. Collectors.groupingBy ===");
        Map<String, List<Order>> byCustomer = orders.stream()
            .collect(Collectors.groupingBy(Order::getCustomer));
        byCustomer.forEach((customer, customerOrders) -> {
            System.out.println(customer + " → " + customerOrders.size() + " order(s)");
        });

        // ── 4. FLATMAP ───────────────────────────────────────────────────────
        System.out.println("\n=== 5. flatMap (One-to-Many expansion) ===");
        // Each customer has multiple order IDs — flatten to a single stream of IDs
        List<String> allOrderIds = byCustomer.values().stream()
            .flatMap(Collection::stream)               // List<List<Order>> → Stream<Order>
            .map(Order::getId)
            .collect(Collectors.toList());
        System.out.println("All order IDs: " + allOrderIds);
    }
}

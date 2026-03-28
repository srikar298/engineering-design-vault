import java.util.*;

/**
 * ============================================================================
 * 🏛️ DIP MASTERY: Clean Architecture Order System
 * ============================================================================
 * A production-grade demonstration of Dependency Inversion at the
 * architectural level — showing how the Domain/Policy layer stays completely
 * isolated from Infrastructure (DB, Payment, Notification) details.
 *
 * Architecture rings:
 *   [DOMAIN]         OrderService, Order, interfaces it defines
 *   [INFRASTRUCTURE] MySQLOrderRepo, StripePaymentGateway, EmailNotifier
 *   [COMPOSITION]    Main — the ONLY place that wires everything together
 *
 * Key proofs:
 *   1. OrderService imports ZERO infrastructure classes.
 *   2. Swapping MySQL → InMemoryDB = zero changes to OrderService.
 *   3. Swapping Stripe → PayPal   = zero changes to OrderService.
 *   4. Unit-testing OrderService  = zero real DB, zero real payment calls.
 * ============================================================================
 */

// ─────────────────────────────────────────────────────────────
// DOMAIN LAYER: Interfaces owned by the high-level policy
// (OrderService defines WHAT it needs — not HOW it's implemented)
// ─────────────────────────────────────────────────────────────

/** Owned by the DOMAIN. Infrastructure must conform to this contract. */
interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(String orderId);
}

/** Owned by the DOMAIN. Payment providers implement this from the outside. */
interface PaymentGateway {
    boolean charge(String customerId, double amount);
}

/** Owned by the DOMAIN. Notification infrastructure implements this. */
interface OrderNotifier {
    void notifyOrderPlaced(Order order);
}

// Domain Entity (pure business value — no infrastructure imports)
class Order {
    private final String id;
    private final String customerId;
    private final double totalAmount;
    private String status;

    public Order(String id, String customerId, double totalAmount) {
        this.id = id;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.status = "PENDING";
    }

    public void markPlaced()    { this.status = "PLACED"; }
    public void markFailed()    { this.status = "PAYMENT_FAILED"; }

    public String getId()         { return id; }
    public String getCustomerId() { return customerId; }
    public double getAmount()     { return totalAmount; }
    public String getStatus()     { return status; }

    @Override public String toString() {
        return String.format("Order[%s | Customer:%s | $%.2f | %s]",
            id, customerId, totalAmount, status);
    }
}

// ─────────────────────────────────────────────────────────────
// DOMAIN SERVICE (High-Level Policy)
// ONLY imports: Order, OrderRepository, PaymentGateway, OrderNotifier
// ZERO imports of any infrastructure class.
// ─────────────────────────────────────────────────────────────
class OrderService {
    private final OrderRepository repository;    // ✅ Depends on abstraction
    private final PaymentGateway  gateway;       // ✅ Depends on abstraction
    private final OrderNotifier   notifier;      // ✅ Depends on abstraction

    // ✅ Constructor Injection — the composition root wires these
    public OrderService(OrderRepository repository,
                        PaymentGateway gateway,
                        OrderNotifier notifier) {
        this.repository = repository;
        this.gateway    = gateway;
        this.notifier   = notifier;
    }

    public Order placeOrder(String customerId, double amount) {
        Order order = new Order("ORD-" + System.nanoTime(), customerId, amount);

        boolean paid = gateway.charge(customerId, amount);
        if (!paid) {
            order.markFailed();
            System.out.println("[OrderService] Payment failed for: " + customerId);
            return order;
        }

        order.markPlaced();
        repository.save(order);
        notifier.notifyOrderPlaced(order);
        return order;
    }

    public Optional<Order> getOrder(String orderId) {
        return repository.findById(orderId);
    }
}


// ─────────────────────────────────────────────────────────────
// INFRASTRUCTURE LAYER: Low-level details depend on domain interfaces
// ─────────────────────────────────────────────────────────────

class MySQLOrderRepository implements OrderRepository {
    private final Map<String, Order> db = new HashMap<>();
    @Override public void save(Order order) {
        db.put(order.getId(), order);
        System.out.println("  [MySQL] Persisted: " + order);
    }
    @Override public Optional<Order> findById(String id) { return Optional.ofNullable(db.get(id)); }
}

/** Used in tests — zero real DB required */
class InMemoryOrderRepository implements OrderRepository {
    private final Map<String, Order> store = new HashMap<>();
    @Override public void save(Order order)                    { store.put(order.getId(), order); }
    @Override public Optional<Order> findById(String id)      { return Optional.ofNullable(store.get(id)); }
}

class StripePaymentGateway implements PaymentGateway {
    @Override public boolean charge(String customerId, double amount) {
        System.out.printf("  [Stripe] Charging $%.2f from Customer %s...%n", amount, customerId);
        return true; // Simulate success
    }
}

class PayPalPaymentGateway implements PaymentGateway {
    @Override public boolean charge(String customerId, double amount) {
        System.out.printf("  [PayPal] Processing $%.2f for Customer %s...%n", amount, customerId);
        return true;
    }
}

/** Used in tests — never sends a real email */
class MockPaymentGateway implements PaymentGateway {
    private boolean shouldSucceed;
    public MockPaymentGateway(boolean shouldSucceed) { this.shouldSucceed = shouldSucceed; }
    @Override public boolean charge(String customerId, double amount) {
        System.out.println("  [MOCK] Simulated payment for: " + customerId + " → " + (shouldSucceed ? "SUCCESS" : "FAIL"));
        return shouldSucceed;
    }
}

class EmailOrderNotifier implements OrderNotifier {
    @Override public void notifyOrderPlaced(Order order) {
        System.out.printf("  [Email] Order confirmation sent to Customer %s for %s%n",
            order.getCustomerId(), order.getId());
    }
}


// ─────────────────────────────────────────────────────────────
// COMPOSITION ROOT: The ONLY place that knows about all layers
// ─────────────────────────────────────────────────────────────
public class DIP_CleanArchitecture {
    public static void main(String[] args) {

        System.out.println("=== ✅ PRODUCTION: Stripe + MySQL + Email ===");
        OrderService productionService = new OrderService(
            new MySQLOrderRepository(),
            new StripePaymentGateway(),
            new EmailOrderNotifier()
        );
        Order order1 = productionService.placeOrder("CUST-001", 1299.99);
        System.out.println("  Result: " + order1);

        System.out.println("\n=== ✅ SWAP: PayPal + MySQL (zero OrderService changes) ===");
        OrderService paypalService = new OrderService(
            new MySQLOrderRepository(),
            new PayPalPaymentGateway(),     // ← Only this line changes
            new EmailOrderNotifier()
        );
        Order order2 = paypalService.placeOrder("CUST-002", 450.00);
        System.out.println("  Result: " + order2);

        System.out.println("\n=== ✅ UNIT TEST: InMemory + Mock (no DB, no network) ===");
        OrderService testService = new OrderService(
            new InMemoryOrderRepository(),  // ← No real DB
            new MockPaymentGateway(true),   // ← Simulated payment: success
            order -> {}                     // ← No-op notifier (lambda)
        );
        Order order3 = testService.placeOrder("TEST-CUST", 99.99);
        System.out.println("  Test Result: " + order3);

        System.out.println("\n=== ✅ UNIT TEST: Simulating payment failure ===");
        OrderService failureTest = new OrderService(
            new InMemoryOrderRepository(),
            new MockPaymentGateway(false),  // ← Simulated payment: fail
            order -> {}
        );
        Order failed = failureTest.placeOrder("TEST-CUST", 5000.00);
        System.out.println("  Failure Test: " + failed);
    }
}

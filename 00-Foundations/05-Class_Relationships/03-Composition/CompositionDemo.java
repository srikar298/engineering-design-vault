import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================================
 * 🏠 COMPOSITION DEMO: Order Management System
 * ============================================================================
 * Key proof: OrderItems and ShippingAddress are COMPOSED (created inside Order).
 * Customer is AGGREGATED (injected from outside).
 * This single class shows BOTH relationships side by side for maximum clarity.
 * ============================================================================
 */

// ── COMPOSED PARTS (no meaning outside their owner) ───────────────────────

// OrderItem: meaningless alone — "2x Laptop for $2500" only makes sense
// in the context of a specific Order at a specific point in time.
class OrderItem {
    private final String productName;
    private final int quantity;
    private final double unitPrice;

    // Package-private constructor: only Order should create OrderItems
    OrderItem(String productName, int quantity, double unitPrice) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0");
        if (unitPrice <= 0) throw new IllegalArgumentException("Price must be > 0");
        this.productName = productName;
        this.quantity    = quantity;
        this.unitPrice   = unitPrice;
    }

    double getSubtotal() { return quantity * unitPrice; }

    @Override public String toString() {
        return String.format("%dx %-20s @ $%.2f = $%.2f",
            quantity, productName, unitPrice, getSubtotal());
    }
}

// ShippingAddress: a snapshot of the address at order-time.
// Changing the customer's profile address later should NOT change this.
class ShippingAddress {
    private final String street;
    private final String city;
    private final String pinCode;

    ShippingAddress(String street, String city, String pinCode) {
        this.street  = street;
        this.city    = city;
        this.pinCode = pinCode;
    }

    @Override public String toString() {
        return street + ", " + city + " - " + pinCode;
    }
}

// ── AGGREGATED PART (independent entity, just referenced) ─────────────────
class Customer {
    private final String id;
    private final String name;

    public Customer(String id, String name) {
        this.id = id; this.name = name;
    }
    public String getId()   { return id; }
    public String getName() { return name; }
    @Override public String toString() { return "Customer[" + id + "] " + name; }
}

// ── THE WHOLE (Order owns its items and address; references its customer) ──
class Order {
    private final String orderId;
    private final Customer customer;          // AGGREGATION: injected from outside
    private final ShippingAddress address;    // COMPOSITION: created internally
    private final List<OrderItem> items;      // COMPOSITION: items are born here
    private String status;

    public Order(String orderId, Customer customer,
                 String street, String city, String pinCode) {
        this.orderId  = orderId;
        this.customer = customer;             // Just storing a reference
        this.address  = new ShippingAddress(street, city, pinCode); // Composed
        this.items    = new ArrayList<>();    // Composed — created here
        this.status   = "CREATED";
    }

    // Behavior that creates composed parts internally
    public void addItem(String product, int qty, double price) {
        if ("PLACED".equals(status)) {
            throw new IllegalStateException("Cannot modify a placed order.");
        }
        items.add(new OrderItem(product, qty, price)); // Composition: new inside
    }

    public void place() {
        if (items.isEmpty()) throw new IllegalStateException("Cannot place an empty order.");
        this.status = "PLACED";
        System.out.println("[ORDER PLACED] " + orderId);
    }

    public double getTotal() {
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    // Return an unmodifiable view — never expose the internal list reference
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void printReceipt() {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("  Order:    " + orderId + "  [" + status + "]");
        System.out.println("  Customer: " + customer.getName());
        System.out.println("  Ship to:  " + address);
        System.out.println("  ──────────────────────────────────────────");
        items.forEach(item -> System.out.println("  " + item));
        System.out.printf("  ══════════════════════════════════════%n");
        System.out.printf("  TOTAL:                          $%7.2f%n", getTotal());
        System.out.println("╚══════════════════════════════════════════╝");
    }
}

// ── EXECUTION ─────────────────────────────────────────────────────────────
public class CompositionDemo {
    public static void main(String[] args) {

        // Customer is created INDEPENDENTLY — it's an independent aggregate
        Customer alice = new Customer("CUST-001", "Alice");

        System.out.println("=== ✅ Building an Order via Composition ===\n");

        // Order creates its OWN ShippingAddress and manages its OWN item list
        Order order = new Order("ORD-2024-001", alice,
                                "12 MG Road", "Bangalore", "560001");

        order.addItem("MacBook Pro 16\"", 1, 2499.99);
        order.addItem("Magic Mouse",      1, 79.99);
        order.addItem("USB-C Hub",        2, 49.99);
        order.place();
        order.printReceipt();

        // ── THE KEY PROOFS ─────────────────────────────────────────────────
        System.out.println("\n=== Lifecycle Proof ===");

        // AGGREGATION proof: Customer Alice outlives any specific Order
        System.out.println("Alice still exists after order is placed: " + alice.getName());

        // COMPOSITION proof: We cannot access OrderItems outside the Order
        // OrderItem item = new OrderItem(...); // ✅ Won't compile outside package
        System.out.println("OrderItems cannot be created outside Order — composition enforces exclusivity.");

        // COMPOSITION proof: Invariant protection
        System.out.println("\n=== Invariant Proof: Cannot modify placed order ===");
        try {
            order.addItem("iPhone", 1, 999.0); // Should throw
        } catch (IllegalStateException e) {
            System.out.println("Blocked: " + e.getMessage());
        }
    }
}

package class_relationships.composition;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>03 - Composition: Strong Ownership / Tied Lifecycle (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> An Order Management System. 
 * An Order "owns" its OrderItems. If an Order is deleted, the OrderItems 
 * have zero meaning and should be deleted too (Cascading Delete).
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Encapsulation:</b> Composed objects are usually created <b>inside</b> the 
 *    parent class. The client should never see the <code>new OrderItem()</code> call.
 * 2. <b>Exclusivity:</b> A composed object belongs to exactly ONE parent.
 * 3. <b>Memory Management:</b> When the parent is Garbage Collected, all composed 
 *    children are also marked for GC.
 * 
 * <b>Edge Cases:</b>
 * - <b>Cascading Deletes:</b> This relationship maps to SQL <code>ON DELETE CASCADE</code>.
 */

class OrderItem {
    private final String sku;
    // Package-private constructor to enforce creation only via Order
    OrderItem(String s) { this.sku = s; }
    @Override public String toString() { return "Item: " + sku; }
}

class Order {
    private final String orderId;
    // --- [INTERVIEW_MVP] (The Composed Collection) ---
    private final List<OrderItem> items = new ArrayList<>();

    public Order(String id) { this.orderId = id; }

    /**
     * [PRODUCTION_ENHANCEMENT]: The Factory Method within Composition.
     * The parent manages the lifecycle of the child.
     */
    public void addItem(String sku) {
        items.add(new OrderItem(sku)); 
    }

    public void displayOrder() {
        System.out.println("Order " + orderId + " contains: " + items);
    }
}

public class CompositionDemoSDE2 {
    public static void main(String[] args) {
        // [INTERVIEW_MVP]: Lifecycle binding
        Order order = new Order("ORD-101");
        order.addItem("LAPTOP-MAC-01");
        order.addItem("MOUSE-LOGI-02");

        order.displayOrder();

        // [PRODUCTION_ENHANCEMENT]: The Death Test
        order = null;
        System.out.println("✅ Order is nulled. Internal items are unreachable and will be GC'd.");
    }
}

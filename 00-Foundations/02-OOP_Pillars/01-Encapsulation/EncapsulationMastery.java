import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================================
 * 🛡️ ENCAPSULATION MASTERY: State Protection, "Tell Don't Ask", & Defensiveness
 * ============================================================================
 */
public class EncapsulationMastery {
    public static void main(String[] args) {
        System.out.println("=== 🛡️ Encapsulation Mastery ===");

        ShoppingCart cart = new ShoppingCart("CRT-9921");

        // 1. "TELL, DON'T ASK" (Logic remains safely inside)
        cart.addItem("MacBook Pro 16", 2500.0);
        cart.addItem("AirPods", 250.0);

        // 2. THE GETTER LEAK TRAP
        System.out.println("\n--- Attempting a Security Breach ---");
        List<String> itemsOut = cart.getSecureItems();
        try {
            itemsOut.add("Hacked Free Item"); // The caller attempts to bypass add logic
        } catch (UnsupportedOperationException e) {
            System.out.println("Success! The Encapsulation blocked the malicious List modification.");
        }

        System.out.println("\nFinal Cart Status:");
        cart.displayCart();
    }
}

class ShoppingCart {
    // 1. DATA HIDING
    private final String cartId;
    private double totalAmount; // Maintained STRICTLY internally
    
    // Collections are notoriously dangerous to expose
    private final List<String> items;

    public ShoppingCart(String cartId) {
        this.cartId = cartId;
        this.totalAmount = 0.0;
        this.items = new ArrayList<>();
    }

    /**
     * 2. DOMAIN BEHAVIOR (Instead of raw Setters)
     * We don't have setTotalAmount(). We have Domain logic.
     * This enforces invariants (you can't add an item without the total increasing).
     */
    public void addItem(String item, double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Cannot add items with negative price.");
        }
        
        // This is safe because it's localized.
        this.items.add(item);
        this.totalAmount += price;
        System.out.println("Added: " + item + " ($" + price + ")");
    }

    /**
     * 3. DEFENSIVE RETURN (The "Collection Leak" Fix)
     * Returning the raw 'items' list passes out the memory reference to our internal Heap.
     * We must wrap it in an unmodifiable view so external callers can observe, but not destroy.
     */
    public List<String> getSecureItems() {
        // Option 1: Collections.unmodifiableList(items) -> Throws exception on modification attempt
        // Option 2: new ArrayList<>(items) -> Gives them a cloned copy they can safely ruin
        return Collections.unmodifiableList(this.items);
    }

    public double getTotalAmount() {
        return totalAmount; // Primitives are passed by value and safe to return raw
    }

    public void displayCart() {
        System.out.printf("Cart: %s | Items: %d | Total: $%.2f%n", 
            cartId, items.size(), totalAmount);
    }
}

import context.ShoppingCart;
import strategy.CreditCardStrategy;
import strategy.CryptoStrategy;
import strategy.PayPalStrategy;

/**
 * <h1>Strategy Pattern Demonstration</h1>
 * 
 * <p>Notice how we dynamically swap out the payment algorithm 
 * at runtime without changing the ShoppingCart class.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   Strategy Pattern: E-Commerce Payment Demo      ");
        System.out.println("==================================================\n");

        ShoppingCart cart = new ShoppingCart();
        
        System.out.println("--- Scenario 1: Customer buys a laptop using Credit Card ---");
        cart.addItem(1200.00);
        cart.addItem(50.00);
        cart.checkout(new CreditCardStrategy("Alice Smith", "1234567890123456"));

        System.out.println("\n--- Scenario 2: Customer buys a coffee using PayPal ---");
        cart.addItem(5.50);
        cart.checkout(new PayPalStrategy("alice.smith@example.com"));

        System.out.println("\n--- Scenario 3: Customer buys a server using Crypto ---");
        cart.addItem(5000.00);
        cart.checkout(new CryptoStrategy("0x1A2B3C4D5E6F7G8H9I0J"));
    }
}

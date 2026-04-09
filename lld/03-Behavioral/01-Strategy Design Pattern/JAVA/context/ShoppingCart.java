package context;

import strategy.IPaymentStrategy;

/**
 * <h1>The Context</h1>
 * 
 * <p>The central class that maintains a reference to a Strategy object. 
 * It delegates the behavior to the Strategy object instead of implementing 
 * multiple versions of the behavior directly within itself.
 */
public class ShoppingCart {
    
    // In a real app we'd have a List<Item>, but we'll focus on the pattern here.
    private double totalAmount = 0;

    public void addItem(double price) {
        totalAmount += price;
        System.out.println("Added item worth $" + price + ". New total: $" + totalAmount);
    }

    /**
     * Executes the strategy. Notice how the ShoppingCart has ZERO IDEA
     * how the payment is actually processed!
     */
    public void checkout(IPaymentStrategy paymentMethod) {
        System.out.println("\nInitiating checkout for total: $" + totalAmount);
        
        // DELEGATION to the Strategy
        paymentMethod.pay(totalAmount);
        
        // Reset cart after successful payment
        totalAmount = 0;
        System.out.println("Checkout complete. Cart emptied.");
    }
}

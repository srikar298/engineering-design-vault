package adapter;

/**
 * <h1>The Adaptee</h1>
 * 
 * <p>A simulated external 3rd-party library. We cannot change this code. 
 * Notice how its API `chargeCreditCard` is totally incompatible with 
 * our internal `processPayment` contract.
 */
public class StripeLegacyAPI {
    
    // Incompatible method signature and return type
    public String chargeCreditCard(String token, int amountCents) {
        System.out.println("   [Stripe API] Processing charge for " + amountCents + " cents...");
        // Simulated network call
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        
        return "SUCCESS_TXN_12345";
    }
}

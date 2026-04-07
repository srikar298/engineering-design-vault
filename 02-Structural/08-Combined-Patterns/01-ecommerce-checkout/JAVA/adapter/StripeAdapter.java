package adapter;

/**
 * <h1>The Object Adapter</h1>
 * 
 * <p>Implements our internal interface, but delegates the heavy lifting 
 * to the incompatible 3rd party library.
 */
public class StripeAdapter implements IPaymentProcessor {
    
    private final StripeLegacyAPI stripeAPI;

    public StripeAdapter() {
        this.stripeAPI = new StripeLegacyAPI();
    }

    @Override
    public boolean processPayment(double amount, String userToken) {
        System.out.println("   [Adapter] Adapting internal payment request to Stripe API...");
        
        // Data Transformation: Convert dollars to cents
        int amountInCents = (int) (amount * 100);
        
        // Delegation
        String response = stripeAPI.chargeCreditCard(userToken, amountInCents);
        
        // Data Transformation: Convert Stripe's String response back to internal Boolean
        return response.startsWith("SUCCESS");
    }
}

package open_closed;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * <h1>OCP: The "Plugin" Principle (SDE-2+ Level)</h1>
 * 
 * Traditional OCP: Just use an Interface.
 * SDE-2+ OCP: Use a REGISTRY. 
 * 
 * Why? If you have a massive switch statement to pick which interface 
 * implementation to use, you are STILL violating OCP in that switch.
 * 
 * A Registry allows you to "register" new behaviors without 
 * changing a single line of the selection logic.
 */

interface PaymentProcessor {
    void process(double amount);
}

// 1. Existing behavior (Closed for modification)
class StripeProcessor implements PaymentProcessor {
    public void process(double amount) { System.out.println("Stripe: $" + amount); }
}

class PayPalProcessor implements PaymentProcessor {
    public void process(double amount) { System.out.println("PayPal: $" + amount); }
}

/**
 * The "Open" part: We can add Crypto without touching the Registry's code
 */
class CryptoProcessor implements PaymentProcessor {
    public void process(double amount) { System.out.println("Crypto: $" + amount); }
}

/**
 * 🛡️ The Registry: 100% OCP Compliant
 */
class PaymentRegistry {
    private static final Map<String, PaymentProcessor> processors = new HashMap<>();

    static {
        // In a real Spring app, these would be auto-registered (@Component)
        processors.put("STRIPE", new StripeProcessor());
        processors.put("PAYPAL", new PayPalProcessor());
    }

    // This method is CLOSED for modification. 
    // It never needs to change even if we add 100 more processors.
    public static Optional<PaymentProcessor> get(String method) {
        return Optional.ofNullable(processors.get(method.toUpperCase()));
    }

    // Dynamic registration (e.g., from a plugin or new module)
    public static void register(String method, PaymentProcessor processor) {
        processors.put(method.toUpperCase(), processor);
    }
}

public class OCPPluginSDE2 {
    public static void main(String[] args) {
        // Adding Crypto dynamically (OCP in action)
        PaymentRegistry.register("CRYPTO", new CryptoProcessor());

        String userChoice = "CRYPTO";
        PaymentRegistry.get(userChoice).ifPresent(p -> p.process(100.0));
        
        System.out.println("✅ OCP Refactored: Plugin Registry achieved.");
    }
}

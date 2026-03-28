/**
 * ============================================================================
 * ✅ REFACTORED: Composition for Deep OCP
 * ============================================================================
 * 
 * SOLUTION:
 * Separate the "Payment Method" from the "Processing Strategy."
 * 
 * THE SENIOR INSIGHT:
 * Composition allows adding "Retry" or "Discount" as independent decorators 
 * or strategies without bloating the Payment types.
 */

interface OCPPaymentMethod {
    void pay(double amount);
}

class CreditCard implements OCPPaymentMethod {
    public void pay(double amount) {
        System.out.println("Paying " + amount + " via Credit Card.");
    }
}

class UpiPayment implements OCPPaymentMethod {
    public void pay(double amount) {
        System.out.println("Paying " + amount + " via UPI.");
    }
}

// --- The Orchestrator (Stable/Closed) ---
class OCPPaymentProcessor {
    private OCPPaymentMethod method;

    public OCPPaymentProcessor(OCPPaymentMethod method) {
        this.method = method;
    }

    public void execute(double amount) {
        // We can add retry/discount logic here as independent behaviors!
        method.pay(amount);
    }
}

public class OCPPaymentRefactored {
    public static void main(String[] args) {
        // Open for extension: Simply plug in a new method.
        OCPPaymentProcessor stripe = new OCPPaymentProcessor(new CreditCard());
        stripe.execute(100.0);

        OCPPaymentProcessor phonePe = new OCPPaymentProcessor(new UpiPayment());
        phonePe.execute(200.0);
    }
}

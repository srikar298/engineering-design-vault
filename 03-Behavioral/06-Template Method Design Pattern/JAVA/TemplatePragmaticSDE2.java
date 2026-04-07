package template;

/**
 * <h1>06 - Template Method: The "Workflow Skeleton" (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> A Global Payment Gateway. 
 * Every payment must: 
 * 1. Validate the user. 
 * 2. Deduct money. 
 * 3. Send notification.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Final Template:</b> The core algorithm method is <code>final</code>. 
 *    Subclasses cannot mess with the sequence of operations (Safety).
 * 2. <b>Hooks:</b> Provides "Hook" methods (empty or default implementations) 
 *    that subclasses <i>can</i> override if they need extra logic.
 * 3. <b>Don't Call Us, We'll Call You:</b> This is the foundation of 
 *    <b>Inversion of Control (IoC)</b>. The parent class controls the 
 *    workflow and calls the child's implementation.
 * 
 * <b>Edge Cases Handled:</b>
 * - <b>Standard Steps:</b> Logic shared by all gateways (like logging) stays in the parent.
 * - <b>Custom Steps:</b> Logic unique to a gateway (like API tokens) goes in the child.
 */

// --- THE ABSTRACT TEMPLATE ---
abstract class AbstractPaymentProcessor {

    // [INTERVIEW_MVP]: The Final Template Method
    public final void processPayment(double amount) {
        validateUser();
        executeDeduction(amount);
        sendReceipt();
        
        // [PRODUCTION_ENHANCEMENT]: Optional Hook
        if (requiresAudit()) {
            auditLog();
        }
    }

    // Shared steps
    private void validateUser() { System.out.println("Validating customer session..."); }
    private void sendReceipt() { System.out.println("Receipt sent to registered email."); }

    // Child must implement
    protected abstract void executeDeduction(double amt);

    // Hooks
    protected boolean requiresAudit() { return false; } // Default: no audit
    protected void auditLog() { System.out.println("Logging sensitive transaction data..."); }
}

// --- CONCRETE IMPLEMENTATIONS ---
class StripeProcessor extends AbstractPaymentProcessor {
    @Override
    protected void executeDeduction(double amt) { System.out.println("Stripe: Charging Credit Card $" + amt); }
}

class PayPalProcessor extends AbstractPaymentProcessor {
    @Override
    protected void executeDeduction(double amt) { System.out.println("PayPal: Deducting from Wallet $" + amt); }

    @Override
    protected boolean requiresAudit() { return true; } // PayPal requires extra auditing
}

/**
 * 🎓 SDE-2+ READINESS CHECK:
 * - Template Method vs Strategy? Template Method uses Inheritance (Compile-time). 
 *   Strategy uses Composition (Runtime). Template is better for fixed workflows.
 */
public class TemplatePragmaticSDE2 {
    public static void main(String[] args) {
        System.out.println("Stripe Flow:");
        new StripeProcessor().processPayment(100.0);

        System.out.println("\nPayPal Flow:");
        new PayPalProcessor().processPayment(200.0);
    }
}

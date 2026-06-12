/**
 * ============================================================================
 * ✅ REFACTORED: Multi-Stakeholder Separation
 * ============================================================================
 * 
 * SOLUTION:
 * Use the "Strategy" or "Delegation" pattern to isolate change axes.
 * 
 * Components:
 * 1. PaymentValidator: Risk/Fraud ownership.
 * 2. PaymentProcessor: Finance/Provider ownership.
 * 3. AuditService: Security/Legal ownership.
 */

// --- Stakeholder: Risk Team ---
class SRPPaymentValidator {
    public boolean isValid(String card) {
        return card.length() >= 16;
    }
}

// --- Stakeholder: Finance/Provider ---
class SRPPaymentProcessor {
    public void charge(double amount, String card) {
        System.out.println("Processing " + amount + " via Stripe Gateway...");
    }
}

// --- Stakeholder: Security/Compliance ---
class AuditService {
    public void log(String event) {
        System.out.println("[SECURE LOG]: " + event);
        // Persistence, retention, and encryption logic hidden here.
    }
}

// --- Orchestrator ---
public class SRPPaymentRefactored {
    private final SRPPaymentValidator validator = new SRPPaymentValidator();
    private final SRPPaymentProcessor processor = new SRPPaymentProcessor();
    private final AuditService auditor = new AuditService();

    public void handle(String card, double amount) {
        if (!validator.isValid(card)) {
            auditor.log("FRAUD ALERT: Invalid card attempt.");
            return;
        }

        processor.charge(amount, card);
        auditor.log("SUCCESS: Payment completed.");
    }

    public static void main(String[] args) {
        new SRPPaymentRefactored().handle("1234567812345678", 500.0);
    }
}

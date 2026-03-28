/**
 * ============================================================================
 * ☣️ VIOLATION: Complex Multi-Stakeholder Violation
 * ============================================================================
 * 
 * SCENARIO:
 * A Payment System that validates, charges, and audits.
 * 
 * STAKEHOLDERS:
 * 1. Risk/Fraud Team -> (Validation rules)
 * 2. Payment Provider/Finance -> (Charging logic, gateway integrations)
 * 3. Security/Compliance/Legal -> (Audit trail requirements)
 */
public class SRPPaymentViolation {

    public void processPayment(String cardInfo, double amount) {
        // --- Responsibility 1: Risk/Validation ---
        if (cardInfo.length() < 16) {
            System.out.println("Invalid Card.");
            return;
        }

        // --- Responsibility 2: Payment Execution (Provider Integration) ---
        System.out.println("Charging $" + amount + " to " + cardInfo);
        // Imagine complex retry logic, gateway timeouts, and encryption here...

        // --- Responsibility 3: Compliance/Audit Logging ---
        // VIOLATION: Logic for WHO can see these logs and HOW long they are kept
        // is now accidentally coupled to the payment execution code.
        System.out.println("[COMPLIANCE AUDIT] Payment processed at " + new java.util.Date());
    }

    public static void main(String[] args) {
        new SRPPaymentViolation().processPayment("1234567812345678", 500.0);
        }
}

/**
 * ============================================================================
 * ☣️ VIOLATION: Combinatorial Explosion (OCP + Inheritance)
 * ============================================================================
 * 
 * SCENARIO:
 * Payment processing with different methods (Card, UPI) and features (Retry).
 * 
 * WHY IS THIS A VIOLATION?
 * If we use inheritance to add features like "Retry" or "Discount," we end up 
 * with an explosion of classes:
 * - CardPayment
 * - RetryableCardPayment
 * - DiscountedCardPayment
 * - RetryableDiscountedCardPayment...
 */
public class OCPPaymentViolation {
    // Imagine a generic Payment class that subclasses grow exponentially.
    // This is "Brittle Design."
    public void process() {
        System.out.println("Payment logic coupled to Type and Feature.");
    }
}

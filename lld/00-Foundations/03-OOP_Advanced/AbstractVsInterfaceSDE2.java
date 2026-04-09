package oop_advanced;

import java.util.List;

/**
 * <h1>Abstract Class vs Interface: The Senior Decision (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> Building a Payment Processing Engine.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Interface (Behavioral Contract):</b> Use when unrelated classes need to 
 *    share a capability (e.g. <code>JSONSerializable</code>). Interfaces have 
 *    NO STATE (except static constants).
 * 2. <b>Abstract Class (Base Identity):</b> Use when classes share a 
 *    <b>Common State</b> or internal logic (e.g. <code>BasePayment</code> 
 *    storing <code>transactionId</code>).
 * 3. <b>Default Methods (Java 8+):</b> Interfaces can now provide default 
 *    behavior, but they still cannot hold non-static fields (Instance State).
 */

// --- [INTERFACE]: Pure Behavior ---
// Use this for "Capabilities" (What can this object do?)
interface Refundable {
    void refund(double amount); // No state allowed here
}

// --- [ABSTRACT CLASS]: Shared Identity & State ---
// Use this for "Identity" (What is this object?)
abstract class PaymentProcessor {
    // --- [PRODUCTION_ENHANCEMENT] (Shared State) ---
    protected final String transactionId; 
    protected String status = "PENDING";

    public PaymentProcessor(String id) { this.transactionId = id; }

    // Shared logic (Template Method pattern)
    public final void process(double amount) {
        validate();
        executePayment(amount);
        this.status = "COMPLETED";
    }

    protected abstract void validate();
    protected abstract void executePayment(double amount);
}

// --- [CONCRETE IMPLEMENTATION] ---
class CreditCardProcessor extends PaymentProcessor implements Refundable {
    public CreditCardProcessor(String id) { super(id); }

    @Override
    protected void validate() { System.out.println("Validating CC expiry..."); }

    @Override
    protected void executePayment(double amount) { System.out.println("Charging CC: " + amount); }

    @Override
    public void refund(double amount) { System.out.println("Refunding to CC..."); }
}

public class AbstractVsInterfaceSDE2 {
    public static void main(String[] args) {
        // [INTERVIEW_MVP]: Identity + Capability demo
        CreditCardProcessor cc = new CreditCardProcessor("TXN-99");
        cc.process(100.0);
        cc.refund(50.0);
        
        System.out.println("✅ Decision: Used Abstract Class for 'TXN State' and Interface for 'Refund Behavior'.");
    }
}

package strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>01 - Strategy: The "Pluggable Algorithm" (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> You are building a <b>Dynamic Pricing Engine</b>. 
 * For 10k users, prices change based on 'User Type' (Student, VIP, Regular).
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>OCP Mastery:</b> Instead of <code>if (type == VIP) price * 0.8</code>, 
 *    encapsulate each logic into a Strategy.
 * 2. <b>Runtime Switching:</b> Unlike Template Method (Compile-time inheritance), 
 *    Strategy uses Composition to swap algorithms at <b>runtime</b>.
 * 3. <b>Functional Java:</b> In modern Java, simple strategies are often just 
 *    Lambdas (Functional Interfaces).
 * 
 * <b>Edge Cases Handled:</b>
 * - <b>Missing Strategy:</b> Provides a default fallback strategy.
 * - <b>Context Safety:</b> The Context class remains immutable once configured.
 */

// --- STRATEGY INTERFACE ---
interface PricingStrategy {
    double calculatePrice(double rawPrice);
}

// --- CONCRETE STRATEGIES ---
class VipStrategy implements PricingStrategy {
    @Override public double calculatePrice(double p) { return p * 0.80; } // 20% Off
}

class StudentStrategy implements PricingStrategy {
    @Override public double calculatePrice(double p) { return p * 0.50; } // 50% Off
}

// --- THE CONTEXT (The Client Interface) ---
class PricingContext {
    // --- [INTERVIEW_MVP] (Composition Reference) ---
    private PricingStrategy strategy;

    public void setStrategy(PricingStrategy s) { this.strategy = s; }

    public double executePricing(double amount) {
        // --- [PRODUCTION_ENHANCEMENT] (Null Safety & Defaulting) ---
        if (strategy == null) {
            System.out.println("No strategy set. Using default pricing.");
            return amount;
        }
        return strategy.calculatePrice(amount);
    }
}

/**
 * 🎓 SDE-2+ READINESS CHECK:
 * - Strategy vs State? Strategy is about 'How to do it'. State is about 'What to do next'.
 * - In Node/TS, this is just passing a function as an argument (High Order Functions).
 */
public class StrategyPragmaticSDE2 {
    public static void main(String[] args) {
        PricingContext engine = new PricingContext();

        // [INTERVIEW_MVP]: Switch at runtime
        engine.setStrategy(new VipStrategy());
        System.out.println("VIP Price: " + engine.executePricing(100.0));

        engine.setStrategy(new StudentStrategy());
        System.out.println("Student Price: " + engine.executePricing(100.0));
        
        // [PRODUCTION_ENHANCEMENT]: Lambda version (Modern Java)
        engine.setStrategy(p -> p * 0.90); // Custom 10% discount
        System.out.println("Promo Price: " + engine.executePricing(100.0));
    }
}

package oop_advanced;

/**
 * <h1>Sealed Classes & Exhaustive Switches (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> Domain modeling for a Result type (Success/Failure). 
 * You want to ensure that no one can add a third "Result" type from 
 * outside your package.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Sealed Classes (Java 17+):</b> Restrict which classes can extend or 
 *    implement them. This creates a "Closed" hierarchy.
 * 2. <b>Algebraic Data Types (ADT):</b> Sealed classes allow Java to behave 
 *    more like functional languages (Kotlin/Rust).
 * 3. <b>Exhaustive Switch:</b> Compilers can now check if you handled every 
 *    possible subtype, eliminating the need for a <code>default</code> case.
 */

// --- [MODERN_OOP]: Sealed Hierarchy ---
sealed interface ServiceResult permits Success, Failure, Pending {}

final class Success implements ServiceResult {
    public String data() { return "Success Data"; }
}

final class Failure implements ServiceResult {
    public String error() { return "System Error 500"; }
}

final class Pending implements ServiceResult {}

public class SealedClassesSDE2 {
    
    /**
     * [PRODUCTION_ENHANCEMENT]: Pattern Matching Switch.
     * Note: This requires Java 17/21.
     */
    public static void handleResult(ServiceResult result) {
        // The compiler knows only 3 subtypes exist!
        switch (result) {
            case Success s -> System.out.println("✅ OK: " + s.data());
            case Failure f -> System.out.println("❌ ERR: " + f.error());
            case Pending p -> System.out.println("⏳ Processing...");
            // No default case needed if switch is exhaustive!
        }
    }

    public static void main(String[] args) {
        handleResult(new Success());
        handleResult(new Failure());
        handleResult(new Pending());
        
        System.out.println("✅ Domain modeling secured via Sealed Classes.");
    }
}

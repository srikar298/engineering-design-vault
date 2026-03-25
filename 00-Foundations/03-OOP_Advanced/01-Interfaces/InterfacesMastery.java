import java.util.function.Function;
import java.util.function.Predicate;

/**
 * ============================================================================
 * 🔌 INTERFACES MASTERY: Role Interfaces, Functional Interfaces & Default Methods
 * ============================================================================
 */

// ----------------------------------------------------------------------------
// 1. ROLE INTERFACES (Small, focused "CAN-DO" contracts)
// ----------------------------------------------------------------------------
interface Printable  { void print(); }
interface Exportable { String exportAsCsv(); }
interface Archivable { void archive(); }

// An Invoice can be Printed, Exported, AND Archived.
class Invoice implements Printable, Exportable, Archivable {
    private final String id;
    private final double amount;

    public Invoice(String id, double amount) {
        this.id = id;
        this.amount = amount;
    }

    @Override public void print() { System.out.println("[PRINT] Invoice: " + id + " | $" + amount); }
    @Override public String exportAsCsv() { return id + "," + amount; }
    @Override public void archive() { System.out.println("[ARCHIVE] Invoice " + id + " sent to cold storage."); }
}

// A simple Report can only be Printed.
class Report implements Printable {
    @Override public void print() { System.out.println("[PRINT] Monthly Report"); }
}


// ----------------------------------------------------------------------------
// 2. DEFAULT METHOD CONFLICT RESOLUTION
// ----------------------------------------------------------------------------
interface SystemLogger {
    default String getTag() { return "[SYSTEM]"; }
}
interface AuditLogger {
    default String getTag() { return "[AUDIT]"; }
}

// Java FORCES this class to resolve the conflict explicitly.
class SecurityService implements SystemLogger, AuditLogger {
    @Override
    public String getTag() {
        // Explicitly choosing which parent's default to delegate to
        return AuditLogger.super.getTag() + SystemLogger.super.getTag();
    }
}


// ----------------------------------------------------------------------------
// 3. FUNCTIONAL INTERFACES (The Lambda Gateway)
// ----------------------------------------------------------------------------
@FunctionalInterface
interface PaymentProcessor {
    void process(double amount); // Exactly ONE abstract method = Functional Interface
}


// ----------------------------------------------------------------------------
// 🚀 EXECUTION
// ----------------------------------------------------------------------------
public class InterfacesMastery {
    public static void main(String[] args) {

        System.out.println("=== 1. Role Interfaces ===");
        Invoice invoice = new Invoice("INV-001", 1500.0);
        invoice.print();
        invoice.archive();
        System.out.println("CSV Export: " + invoice.exportAsCsv());

        System.out.println("\n=== 2. Default Method Conflict Resolution ===");
        SecurityService svc = new SecurityService();
        System.out.println("Resolved tag: " + svc.getTag());

        System.out.println("\n=== 3. Functional Interfaces & Lambdas ===");
        // A lambda IS a Functional Interface implementation — no class needed!
        PaymentProcessor stripeProcessor = amount ->
            System.out.println("[STRIPE] Charging $" + amount + " via REST API...");

        PaymentProcessor cryptoProcessor = amount ->
            System.out.println("[CRYPTO] Writing $" + amount + " to Blockchain...");

        stripeProcessor.process(250.0);
        cryptoProcessor.process(250.0);

        System.out.println("\n=== 4. Built-in Functional Interfaces ===");
        Predicate<Double> isHighValue = amount -> amount > 1000.0;
        Function<Double, String> formatter = amount -> String.format("$%.2f", amount);

        double txn = 1500.0;
        System.out.println("Is High Value: " + isHighValue.test(txn));
        System.out.println("Formatted:     " + formatter.apply(txn));
    }
}

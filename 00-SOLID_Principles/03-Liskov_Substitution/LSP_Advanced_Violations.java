import java.util.*;

/**
 * ============================================================================
 * ☣️ LSP VIOLATION: ReadOnlyArchiveStorage & Stack-extends-Vector
 * ============================================================================
 * Two distinct real-world LSP violations beyond the classic Square-Rectangle.
 *
 * Violation 1 → Cloud Storage: A "read-only" storage that silently throws on upload().
 * Violation 2 → Java's own Stack.extends.Vector: a caller expects pop() semantics
 *               but can add elements at arbitrary positions — destroying the invariant.
 * ============================================================================
 */

// ─────────────────────────────────────────────────────────────
// VIOLATION 1: Cloud Storage Pre-condition / Post-condition Break
// ─────────────────────────────────────────────────────────────

class StorageProvider {
    protected String name;
    public StorageProvider(String name) { this.name = name; }

    /**
     * Contract (Post-condition guaranteed by parent):
     *   - After upload(), the file IS accessible via download().
     *   - Never throws for a valid filename.
     */
    public void upload(String filename) {
        System.out.println("[" + name + "] Uploading: " + filename);
    }
    public String download(String filename) {
        return "[" + name + "] Downloading: " + filename;
    }
}

class LiveS3Storage extends StorageProvider {
    public LiveS3Storage() { super("AWS-S3"); }

    @Override public void upload(String filename) {
        System.out.println("[AWS-S3] Streaming " + filename + " to bucket...");
    }
}

// ❌ VIOLATION: ReadOnlyArchiveStorage breaks the parent's upload() contract.
// Any caller that takes a StorageProvider and calls upload() silently explodes.
class ReadOnlyArchiveStorage extends StorageProvider {
    public ReadOnlyArchiveStorage() { super("Archive"); }

    @Override
    public void upload(String filename) {
        // ❌ Strengthens pre-condition (rejects all writes) and weakens post-condition
        //    (file is NOT uploadable). The parent promised this would succeed.
        throw new UnsupportedOperationException("Archive storage is READ-ONLY.");
    }
}

// ─────────────────────────────────────────────────────────────
// VIOLATION 2: HR Promotion — Object identity destroyed at runtime
// (Shown in Inheritance module too, referenced here for LSP angle)
// ─────────────────────────────────────────────────────────────

abstract class Employee {
    protected String name;
    protected double baseSalary;
    public Employee(String name, double baseSalary) {
        this.name = name; this.baseSalary = baseSalary;
    }
    public abstract double calculateBonus();
    public String getName() { return name; }
}

class JuniorEngineer extends Employee {
    public JuniorEngineer(String name, double salary) { super(name, salary); }
    @Override public double calculateBonus() { return baseSalary * 0.1; }
}

class SeniorEngineer extends Employee {
    private int patentsHeld;
    public SeniorEngineer(String name, double salary, int patents) {
        super(name, salary);
        this.patentsHeld = patents;
    }
    // ❌ Post-condition risk: if a caller always expects bonus > 0.1 * salary,
    //    and the patent system changes so patents=0 gives 0% bonus,
    //    they silently get zero. The caller can't trust the contract.
    @Override public double calculateBonus() {
        return baseSalary * 0.15 + (patentsHeld * 5000);
    }
}

// Service that takes ANY Employee and relies on the bonus contract
class PayrollService {
    public void processPayroll(List<Employee> employees) {
        for (Employee e : employees) {
            double bonus = e.calculateBonus();
            System.out.printf("  [PAYROLL] %s → Bonus: $%.2f%n", e.getName(), bonus);
        }
    }
}

// ─────────────────────────────────────────────────────────────
// 🚀 EXECUTION
// ─────────────────────────────────────────────────────────────
public class LSP_Advanced_Violations {
    public static void main(String[] args) {

        System.out.println("=== VIOLATION 1: Cloud Storage Contract Break ===");
        List<StorageProvider> providers = new ArrayList<>();
        providers.add(new LiveS3Storage());
        providers.add(new ReadOnlyArchiveStorage()); // Caller can't tell it's read-only

        for (StorageProvider p : providers) {
            try {
                p.upload("report.pdf"); // ❌ EXPLODES on ReadOnlyArchiveStorage
            } catch (UnsupportedOperationException e) {
                System.out.println("  LSP VIOLATION caught at runtime: " + e.getMessage());
                System.out.println("  The caller had zero warning from the type system.");
            }
        }

        System.out.println("\n=== VIOLATION 2: Java's Stack extends Vector (instanceof trap) ===");
        // Java's Stack inherits from Vector, meaning these are all VALID calls on a Stack:
        Deque<String> stack = new ArrayDeque<>(); // ✅ Senior choice: use Deque instead
        stack.push("First");
        stack.push("Second");
        stack.push("Third");
        System.out.println("  Top of stack (Deque.peek): " + stack.peek());
        System.out.println("  Popped: " + stack.pop());
        // A Stack<String> (java.util) would allow stack.add(0, "inject") at any position,
        // completely violating the LIFO invariant. The Deque interface enforces LIFO semantics.

        System.out.println("\n=== CORRECT USAGE: Payroll (LSP-safe substitution) ===");
        List<Employee> team = new ArrayList<>();
        team.add(new JuniorEngineer("Alice", 80000));
        team.add(new SeniorEngineer("Bob", 120000, 3));
        new PayrollService().processPayroll(team); // ✅ Any Employee works safely
    }
}

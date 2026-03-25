import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * 🎓 AGGREGATION DEMO: University System
 * ============================================================================
 * Key proof: Deleting (nulling) a Department does NOT destroy the Professors.
 * Professors are injected (aggregated), not created (composed).
 * ============================================================================
 */

// ── PROFESSOR ─────────────────────────────────────────────────────────────
// An independent entity. Can exist without any Department.
class Professor {
    private final String name;
    private final String expertise;

    public Professor(String name, String expertise) {
        this.name = name;
        this.expertise = expertise;
    }
    public String getName()      { return name; }
    public String getExpertise() { return expertise; }
    @Override public String toString() {
        return "Prof. " + name + " (" + expertise + ")";
    }
}

// ── DEPARTMENT ────────────────────────────────────────────────────────────
// The "whole" in the aggregation. It does NOT own the Professor's lifecycle.
class Department {
    private final String name;
    // Aggregation: professors are INJECTED, not created here.
    private final List<Professor> professors;

    public Department(String name, List<Professor> professors) {
        this.name = name;
        // Defensive copy: we copy the list, we don't hold the caller's reference.
        this.professors = new ArrayList<>(professors);
    }

    public void addProfessor(Professor p) { professors.add(p); }
    public String getName()               { return name; }
    public List<Professor> getProfessors(){ return List.copyOf(professors); }

    public void showRoster() {
        System.out.println("  Department: " + name);
        professors.forEach(p -> System.out.println("    - " + p));
    }
}

// ── EXECUTION ─────────────────────────────────────────────────────────────
public class AggregationDemo {
    public static void main(String[] args) {

        // 1. Professors created INDEPENDENTLY — they exist before any Department
        Professor profKumar = new Professor("Kumar",  "Distributed Systems");
        Professor profLee   = new Professor("Lee",    "Machine Learning");
        Professor profGupta = new Professor("Gupta",  "Algorithms");

        System.out.println("=== Professors exist independently ===");
        System.out.println("  " + profKumar);
        System.out.println("  " + profLee);
        System.out.println("  " + profGupta);

        // 2. Departments are created by injecting professors
        List<Professor> csProfessors = new ArrayList<>();
        csProfessors.add(profKumar);
        csProfessors.add(profLee);
        Department csDept = new Department("Computer Science", csProfessors);

        List<Professor> mathProfessors = new ArrayList<>();
        mathProfessors.add(profGupta);
        mathProfessors.add(profLee); // Prof. Lee belongs to BOTH departments!
        Department mathDept = new Department("Mathematics", mathProfessors);

        System.out.println("\n=== Department Rosters ===");
        csDept.showRoster();
        mathDept.showRoster();

        // 3. THE KEY PROOF: A Professor can belong to MULTIPLE aggregates
        System.out.println("\n=== Prof. Lee is in both departments (shared aggregation) ===");
        System.out.println("  CS Dept has Lee:   " + csDept.getProfessors().contains(profLee));
        System.out.println("  Math Dept has Lee: " + mathDept.getProfessors().contains(profLee));

        // 4. THE LIFECYCLE PROOF: "Deleting" the CS Department doesn't kill its professors
        System.out.println("\n=== Simulating CS Department shutdown ===");
        csDept = null; // The Department object is eligible for GC
        System.gc();   // Hint to GC (not guaranteed but illustrative)

        System.out.println("  CS Department reference: nullified");
        System.out.println("  Prof. Kumar still alive: " + profKumar);  // ✅ Still exists
        System.out.println("  Prof. Lee still in Math: " + mathDept.getProfessors().contains(profLee)); // ✅
    }
}

/**
 * ============================================================================
 * 🏗️ INHERITANCE vs. COMPOSITION: Choosing the right "IS-A" vs "HAS-A"
 * ============================================================================
 */

// ----------------------------------------------------------------------------
// ❌ 1. THE INHERITANCE APPROACH (Tight Coupling & Rigid Identity)
// ----------------------------------------------------------------------------
class TraditionalEmployee {
    String name;
    public TraditionalEmployee(String name) { this.name = name; }
    
    public void performDuties() {
        System.out.println(name + " is doing basic employee tasks.");
    }
}

// Rigid "IS-A" relationship.
class TraditionalManager extends TraditionalEmployee {
    public TraditionalManager(String name) { super(name); }
    
    @Override
    public void performDuties() {
        System.out.println(name + " is conducting 1-on-1s and managing the team.");
    }
}


// ----------------------------------------------------------------------------
// ✅ 2. THE COMPOSITION APPROACH (Loose Coupling & Runtime Flexibility)
// ----------------------------------------------------------------------------
interface Role {
    void executeDuties(String name);
}

class StandardRole implements Role {
    public void executeDuties(String name) { 
        System.out.println(name + " is doing basic employee tasks."); 
    }
}

class ManagerRole implements Role {
    public void executeDuties(String name) { 
        System.out.println(name + " is conducting 1-on-1s and managing the team."); 
    }
}

class ModernEmployee {
    private String name;
    private Role currentRole; // HAS-A relationship (Composition)

    public ModernEmployee(String name, Role startingRole) {
        this.name = name;
        this.currentRole = startingRole;
    }

    // SWAPPABLE BEHAVIOR at runtime! No object destruction required.
    public void promoteTo(Role newRole) {
        System.out.printf(">> HR ACTION: Promoting %s...%n", name);
        this.currentRole = newRole; 
    }

    public void work() {
        currentRole.executeDuties(name);
    }
}

// ----------------------------------------------------------------------------
// 🚀 EXECUTION
// ----------------------------------------------------------------------------
public class InheritanceMastery {
    public static void main(String[] args) {
        System.out.println("=== 🏗️ The Problem with Rigid Inheritance ===");

        // Inheritance: Fixed at compile time. 
        TraditionalEmployee alice = new TraditionalEmployee("Alice");
        alice.performDuties();

        // ❌ Problem: Alice is promoted to Manager. We cannot mathematically 
        // change her object type from TraditionalEmployee to TraditionalManager.
        // We must destroy the 'alice' object and birth a new one, violating identity.
        System.out.println(">> HR ACTION: Promoting Alice... (Forced memory destruction)");
        TraditionalManager newAlice = new TraditionalManager("Alice");
        newAlice.performDuties();


        System.out.println("\n=== 🎯 The Solution via Composition ===");
        
        // Composition: Flexible at runtime.
        ModernEmployee bob = new ModernEmployee("Bob", new StandardRole());
        bob.work();

        // ✅ Solution: Bob gets promoted. He just swaps his "Toolbelt" (Role) 
        // without his fundamental Object Identity changing.
        bob.promoteTo(new ManagerRole());
        bob.work();
    }
}

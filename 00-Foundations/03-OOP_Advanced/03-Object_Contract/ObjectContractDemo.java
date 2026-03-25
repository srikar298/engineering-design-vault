import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * ============================================================================
 * 🤝 OBJECT CONTRACT MASTERY: equals, hashCode & The Ghost Object Bug
 * ============================================================================
 */

// ----------------------------------------------------------------------------
// ❌ BROKEN: Missing hashCode override
// ----------------------------------------------------------------------------
class BrokenUser {
    String id;
    String name;

    public BrokenUser(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrokenUser that = (BrokenUser) o;
        return Objects.equals(id, that.id);
    }
    // ❌ hashCode is NOT overridden — uses default memory address hash!
}


// ----------------------------------------------------------------------------
// ✅ CORRECT: Both equals AND hashCode overridden on same field (id)
// ----------------------------------------------------------------------------
class ProperUser {
    private final String id; // Immutable identity field — safe for hashCode
    private String name;     // Mutable display field — NOT used in hashCode

    public ProperUser(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Copy Constructor (Senior alternative to Object.clone())
    public ProperUser(ProperUser other) {
        this.id = other.id;
        this.name = other.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProperUser that = (ProperUser) o;
        return Objects.equals(id, that.id); // Only identity field
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // SAME field as equals — Contract maintained
    }

    public void setName(String name) { this.name = name; }

    @Override
    public String toString() { return "User{id='" + id + "', name='" + name + "'}"; }
}


// ----------------------------------------------------------------------------
// 🚀 EXECUTION
// ----------------------------------------------------------------------------
public class ObjectContractDemo {
    public static void main(String[] args) {

        System.out.println("=== 1. The Ghost Object Bug (Missing hashCode) ===");
        BrokenUser b1 = new BrokenUser("42", "Alice");
        BrokenUser b2 = new BrokenUser("42", "Alice"); // Logically the same

        Map<BrokenUser, String> brokenCache = new HashMap<>();
        brokenCache.put(b1, "Premium Member");

        System.out.println("b1.equals(b2): " + b1.equals(b2)); // true
        System.out.println("Cache with b2: " + brokenCache.get(b2)); // ❌ NULL — ghost!
        // Explanation: b1 and b2 have different hashCodes (memory addresses),
        // so HashMap looks in the WRONG bucket and never finds the value.


        System.out.println("\n=== 2. Correct Contract (equals + hashCode) ===");
        ProperUser p1 = new ProperUser("42", "Alice");
        ProperUser p2 = new ProperUser("42", "Alice"); // Logically the same

        Map<ProperUser, String> properCache = new HashMap<>();
        properCache.put(p1, "Premium Member");

        System.out.println("p1.equals(p2): " + p1.equals(p2)); // true
        System.out.println("Cache with p2: " + properCache.get(p2)); // ✅ "Premium Member"

        Set<ProperUser> deduplicatedSet = new HashSet<>();
        deduplicatedSet.add(p1);
        deduplicatedSet.add(p2);
        System.out.println("Set size (should be 1): " + deduplicatedSet.size()); // ✅ 1


        System.out.println("\n=== 3. Copy Constructor vs Object.clone() ===");
        ProperUser original = new ProperUser("99", "Bob");
        ProperUser copy = new ProperUser(original); // Deep copy via Copy Constructor
        copy.setName("Robert");

        System.out.println("Original: " + original); // Bob — unaffected
        System.out.println("Copy:     " + copy);      // Robert
    }
}

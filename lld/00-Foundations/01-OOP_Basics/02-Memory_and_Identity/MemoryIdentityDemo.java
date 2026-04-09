import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A comprehensive demonstration of Memory, Identity, Pass-by-Value, and Hash Contracts.
 */
public class MemoryIdentityDemo {

    public static void main(String[] args) {
        System.out.println("--- 1. Identity (==) vs. Equality (.equals()) ---");
        UserProfile u1 = new UserProfile("USR-123", "Alice");
        UserProfile u2 = new UserProfile("USR-123", "Alice"); // Different physical obj, same data

        System.out.println("Identity (==)       : " + (u1 == u2));       // FALSE: Two different heap allocations
        System.out.println("Equality (.equals()): " + u1.equals(u2));    // TRUE: Business logic says they match

        System.out.println("\n--- 2. The String Pool Trap ---");
        String s1 = "Hello";
        String s2 = "Hello";
        String s3 = new String("Hello");

        System.out.println("s1 == s2          : " + (s1 == s2)); // TRUE! JVM reused the String Pool memory.
        System.out.println("s1 == s3          : " + (s1 == s3)); // FALSE! 'new' forced a fresh heap allocation.

        System.out.println("\n--- 3. Pass-by-Value (Passing the Reference Value) ---");
        System.out.println("Before reassign: " + u1.getName());
        attemptToReassignObject(u1); 
        System.out.println("After reassign : " + u1.getName()); // It didn't change! The physical object is safe.

        attemptToModifyState(u1);
        System.out.println("After modify   : " + u1.getName()); // It changed! The copy of the pointer modified the Heap.

        System.out.println("\n--- 4. The hashCode() Contract ---");
        // We put u1 in the map.
        Map<UserProfile, String> cache = new HashMap<>();
        cache.put(u1, "Logged-in Session Data");

        // We try to retrieve it using u2 (which is .equals() to u1)
        // If we hadn't overridden hashCode(), this lookup would fail!
        System.out.println("User session located: " + cache.get(u2));
    }

    // Attempting to reassign the pointer to a completely new object
    public static void attemptToReassignObject(UserProfile profile) {
        // 'profile' here is just a local stack copy of the pointer.
        // Reassignment only affects this local variable, not the caller's variable!
        profile = new UserProfile("USR-999", "HackedName");
    }

    // Modifying the state of the object the pointer addresses
    public static void attemptToModifyState(UserProfile profile) {
        // Using the pointer copy to reach into the Heap and modify the payload.
        profile.setName("Alice_Modified");
    }
}

class UserProfile {
    private final String id;
    private String name;

    public UserProfile(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // THE SENIOR CONTRACT: Equality by Business Logic
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Performance optimization (identity check first)
        if (o == null || getClass() != o.getClass()) return false; // Type safety checking
        UserProfile that = (UserProfile) o;
        return id.equals(that.id); // Business equality is strictly bound to ID
    }

    // THE SENIOR CONTRACT: HashCode MUST match equals
    @Override
    public int hashCode() {
        return Objects.hash(id); // Generates hash based exclusively on ID
    }
}

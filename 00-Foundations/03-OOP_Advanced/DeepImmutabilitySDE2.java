package oop_advanced;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <h1>Deep Immutability & Records (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> A UserProfile that must be 100% immutable to prevent 
 * state corruption in a multi-threaded system.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Shallow vs. Deep:</b> Making a List field <code>final</code> only prevents 
 *    reassigning the list reference. It does NOT prevent adding items to the list.
 * 2. <b>Defensive Copying:</b> Always copy mutable objects in the constructor 
 *    and return unmodifiable views in getters.
 * 3. <b>Records (Java 14+):</b> Use Records for pure data carriers. They are 
 *    natively immutable and concise.
 */

// --- [PRODUCTION_ENHANCEMENT]: Manual Deep Immutability ---
final class ImmutableUser {
    private final String name;
    private final List<String> roles; // Mutable object reference

    public ImmutableUser(String name, List<String> roles) {
        this.name = name;
        // --- [INTERVIEW_MVP] (Defensive Copy) ---
        // Prevents the caller from modifying the list after passing it.
        this.roles = new ArrayList<>(roles);
    }

    public String getName() { return name; }

    public List<String> getRoles() {
        // --- [INTERVIEW_MVP] (Unmodifiable View) ---
        // Prevents the caller from modifying our internal list.
        return Collections.unmodifiableList(roles);
    }
}

// --- [MODERN_OOP]: Java Records ---
// Standard for SDE-2+ to know modern Java syntax.
record UserRecord(String name, List<String> roles) {
    // Record Constructor (Compact)
    public UserRecord {
        // Records are shallowly immutable by default. 
        // We still need a defensive copy for the list!
        roles = List.copyOf(roles); 
    }
}

public class DeepImmutabilitySDE2 {
    public static void main(String[] args) {
        List<String> roles = new ArrayList<>();
        roles.add("ADMIN");

        ImmutableUser user = new ImmutableUser("Alice", roles);
        
        // Try to hack the immutability
        roles.add("HACKER"); // Won't affect user.roles due to defensive copy
        
        try {
            user.getRoles().add("MANAGER"); // Will throw UnsupportedOperationException
        } catch (Exception e) {
            System.out.println("✅ Immutability Shield working: " + e.getClass().getSimpleName());
        }

        System.out.println("✅ Deep Immutability achieved.");
    }
}

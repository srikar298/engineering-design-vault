package oop_advanced;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>Static Factory Methods: The "Effective Java" Basic (SDE-2+ Level)</h1>
 * 
 * Before you learn Factory Pattern, you MUST understand Static Factory Methods.
 * This is "Item 1" in Joshua Bloch's "Effective Java".
 * 
 * Why prefer static factory methods over constructors?
 * 1. Unlike constructors, they HAVE NAMES (meaningful creation).
 * 2. Unlike constructors, they don't have to create a NEW object (caching).
 * 3. Unlike constructors, they can return any SUBTYPE of their return type.
 */
public class UserBasic {

    private final String name;
    private final String role;

    // 1. PRIVATE CONSTRUCTOR: Force users to use our static factory
    private UserBasic(String name, String role) {
        this.name = name;
        this.role = role;
    }

    /**
     * ✅ Meaningful Name: Better than just 'new User("John", "ADMIN")'
     */
    public static UserBasic createAdmin(String name) {
        return new UserBasic(name, "ADMIN");
    }

    public static UserBasic createGuest(String name) {
        return new UserBasic(name, "GUEST");
    }

    /**
     * ✅ Instance Caching: Constructors ALWAYS create a new object. 
     * Static factories can return a pre-existing one.
     */
    private static final Map<String, UserBasic> CACHE = new HashMap<>();

    public static UserBasic getCachedUser(String name) {
        return CACHE.computeIfAbsent(name, n -> new UserBasic(n, "GUEST"));
    }

    /**
     * ✅ Returning Subtypes: A constructor can only return its own class. 
     * A static factory can return an interface or any subclass.
     */
    public static UserInterface createSpecialUser() {
        // We could return SpecialUser, PremiumUser, etc., hidden from the client
        return new SpecialUser();
    }

    @Override
    public String toString() {
        return "User [name=" + name + ", role=" + role + "]";
    }
}

interface UserInterface { void doWork(); }
class SpecialUser implements UserInterface { @Override public void doWork() { System.out.println("Special work"); } }

/**
 * 🎓 INTERVIEW TIP (SDE-2+):
 * "I prefer static factory methods because they make my code self-documenting. 
 * Instead of multiple overloaded constructors (Telescoping Constructor), I use 
 * descriptive methods like .of(), .from(), or .valueOf(). This is the same pattern 
 * used in the Java Standard Library (e.g., Optional.of(), List.of())."
 */
class Main {
    public static void main(String[] args) {
        UserBasic admin = UserBasic.createAdmin("Alice");
        UserBasic guest = UserBasic.getCachedUser("Bob");
        UserBasic sameGuest = UserBasic.getCachedUser("Bob");

        System.out.println(admin);
        System.out.println("Are guests the same instance? " + (guest == sameGuest)); // True! Caching works.
    }
}

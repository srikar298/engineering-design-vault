import java.util.ArrayList;
import java.util.List;

/**
 * A comprehensive demonstration of Senior-level Constructor patterns.
 */
public class ConstructorDeepDive {
    public static void main(String[] args) {
        System.out.println("--- 1. Overloading & Chaining ---");
        UserProfile beginner = new UserProfile("junior_dev");
        UserProfile expert = new UserProfile("senior_dev", "Java", 10);
        beginner.display();
        expert.display();

        System.out.println("\n--- 2. Copy Constructor ---");
        // We want to clone 'expert', but modify the clone's permissions safely.
        UserProfile expertClone = new UserProfile(expert);
        expertClone.addPermission("ADMIN");
        System.out.println("Original Expert permissions: " + expert.getPermissions());
        System.out.println("Cloned Expert permissions: " + expertClone.getPermissions());

        System.out.println("\n--- 3. Static Factory Method ---");
        UserProfile guest = UserProfile.createGuestAccount();
        guest.display();

        System.out.println("\n--- 4. Private Constructor (Utility Class) ---");
        // MathUtils utils = new MathUtils(); // ERROR: Constructor is private
        System.out.println("PI Value: " + MathUtils.PI);
    }
}

class UserProfile {
    private final String username;  // Immutable
    private final String language;  // Immutable
    private int experienceYears;
    private final List<String> permissions; // Mutable List

    // --------------------------------------------------------
    // THE MASTER CONSTRUCTOR
    // Single Source of Truth for validation and assignment.
    // --------------------------------------------------------
    public UserProfile(String username, String language, int experienceYears, List<String> permissions) {
        // Validation (The Gatekeeper)
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        this.username = username;
        this.language = language;
        this.experienceYears = experienceYears;
        
        // Deep copy the list to prevent external modification (Reference leak)
        this.permissions = new ArrayList<>(permissions);
    }

    // --------------------------------------------------------
    // CONSTRUCTOR OVERLOADING & CHAINING
    // --------------------------------------------------------
    // Convenience constructor 1
    public UserProfile(String username, String language, int experienceYears) {
        // Delegates to Master Constructor
        this(username, language, experienceYears, new ArrayList<>());
    }

    // Convenience constructor 2
    public UserProfile(String username) {
        // Defaults to "Unknown" language and 0 years
        this(username, "Unknown", 0);
    }

    // --------------------------------------------------------
    // COPY CONSTRUCTOR
    // --------------------------------------------------------
    public UserProfile(UserProfile source) {
        // We reuse the Master Constructor to ensure validation logic runs.
        // We pass the source's data in.
        this(source.username, source.language, source.experienceYears, source.permissions);
    }

    // --------------------------------------------------------
    // STATIC FACTORY METHOD
    // --------------------------------------------------------
    public static UserProfile createGuestAccount() {
        return new UserProfile("Guest_" + System.currentTimeMillis(), "None", 0);
    }

    // --- Behavior ---
    public void addPermission(String perm) {
        this.permissions.add(perm);
    }

    public List<String> getPermissions() {
        // Defensive copy to prevent external mutation
        return new ArrayList<>(permissions);
    }

    public void display() {
        System.out.printf("User: %s | Lang: %s | Exp: %d yrs | Perms: %s%n", 
            username, language, experienceYears, permissions);
    }
}

/**
 * Utility class demonstrating a Private Constructor.
 */
class MathUtils {
    public static final double PI = 3.14159;

    // Private constructor prevents instantiation
    private MathUtils() {
        throw new AssertionError("Cannot instantiate utility class MathUtils");
    }

    public static double circleArea(double radius) {
        return PI * radius * radius;
    }
}

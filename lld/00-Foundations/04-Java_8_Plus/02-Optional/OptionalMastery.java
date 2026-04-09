import java.util.Optional;

/**
 * ============================================================================
 * 📦 OPTIONAL MASTERY: Eliminating NullPointerException
 * ============================================================================
 */
public class OptionalMastery {

    static class Address {
        private final String city;
        public Address(String city) { this.city = city; }
        public String getCity() { return city; }
    }

    static class User {
        private final String id;
        private final String name;
        private final boolean active;
        private final Address address; // nullable — not every user has an address

        public User(String id, String name, boolean active, Address address) {
            this.id = id; this.name = name;
            this.active = active; this.address = address;
        }
        public String getId()     { return id; }
        public String getName()   { return name; }
        public boolean isActive() { return active; }
        public Optional<Address> getAddress() { return Optional.ofNullable(address); }
    }

    // Simulated Repository
    static Optional<User> findUserById(String id) {
        if ("USR-1".equals(id)) return Optional.of(new User("USR-1", "Alice", true, new Address("Mumbai")));
        if ("USR-2".equals(id)) return Optional.of(new User("USR-2", "Bob", false, null));
        return Optional.empty(); // Not found
    }


    public static void main(String[] args) {

        // ── 1. THE ANTI-PATTERN: Null check ladders ──────────────────────────
        System.out.println("=== ❌ Anti-Pattern: Null Ladder ===");
        // User found = ... (imagine null checks)
        // if (found != null && found.isActive() && found.getAddress() != null) { ... }
        System.out.println("[Skipped for brevity — the classic NPE trap]");


        // ── 2. THE CORRECT WAY: Optional chaining ────────────────────────────
        System.out.println("\n=== ✅ Optional Chain: Active user's city ===");

        String city = findUserById("USR-1")
            .filter(User::isActive)                 // Only active users
            .flatMap(User::getAddress)               // Unwrap Optional<Address>
            .map(Address::getCity)                   // Transform to city string
            .orElse("City not available");           // Safe fallback
        System.out.println("USR-1 city: " + city);  // Mumbai

        // ── 3. INACTIVE USER ─────────────────────────────────────────────────
        System.out.println("\n=== Inactive user (filter removes them) ===");
        String inactiveCity = findUserById("USR-2")
            .filter(User::isActive)                 // Bob is inactive → filtered out
            .flatMap(User::getAddress)
            .map(Address::getCity)
            .orElse("City not available");
        System.out.println("USR-2 city: " + inactiveCity); // City not available

        // ── 4. NOT FOUND ──────────────────────────────────────────────────────
        System.out.println("\n=== User not found → orElseThrow ===");
        try {
            findUserById("USR-99")
                .orElseThrow(() -> new RuntimeException("User USR-99 not found in system"));
        } catch (RuntimeException e) {
            System.out.println("Caught typed exception: " + e.getMessage());
        }

        // ── 5. orElse vs orElseGet ────────────────────────────────────────────
        System.out.println("\n=== orElse (eager) vs orElseGet (lazy) ===");

        // orElse ALWAYS evaluates the fallback expression — even when value is present!
        findUserById("USR-1")
            .map(User::getName)
            .orElse(computeDefault()); // computeDefault() runs even though USR-1 exists!

        // orElseGet is LAZY — only runs the supplier if value is absent
        findUserById("USR-1")
            .map(User::getName)
            .orElseGet(() -> computeDefault()); // computeDefault() does NOT run

        System.out.println("Note: orElseGet avoids the expensive call when value exists.");

        // ── 6. ifPresent for side effects ────────────────────────────────────
        System.out.println("\n=== ifPresent side-effect ===");
        findUserById("USR-1").ifPresent(u ->
            System.out.println("Welcome back, " + u.getName() + "!")
        );
        findUserById("USR-99").ifPresent(u ->
            System.out.println("This line will NOT print")
        );
    }

    // Simulates an expensive fallback (e.g., DB call)
    static String computeDefault() {
        System.out.println("  [EXPENSIVE computeDefault() called!]");
        return "Anonymous";
    }
}

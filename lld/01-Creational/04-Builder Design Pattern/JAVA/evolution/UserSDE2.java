package evolution;

/**
 * <h1>04 - Builder: The "Atomic Consistency" Pattern (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> A User registration DTO. Users have 20+ fields (bio, theme, 
 * notification settings). Some are mandatory (email, username), others are optional.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Immutability:</b> All fields in the target class are <code>final</code>. 
 *    The object is thread-safe after creation.
 * 2. <b>Validation:</b> The <code>.build()</code> method enforces business rules 
 *    before the object is "born."
 * 3. <b>Fluent API:</b> Improves developer experience (DX) and readability.
 * 
 * <b>Edge Cases Handled:</b>
 * - <b>Inconsistent State:</b> Prevents creating a user with only half their name.
 * - <b>Defaults:</b> Handles optional fields by providing sane defaults in the Builder.
 * - <b>Cross-Field Validation:</b> Ensures 'age' is positive if provided.
 */
public class UserSDE2 {

    private final String userId;   // Mandatory
    private final String email;    // Mandatory
    private final String bio;      // Optional
    private final int age;         // Optional

    private UserSDE2(Builder builder) {
        this.userId = builder.userId;
        this.email = builder.email;
        this.bio = builder.bio;
        this.age = builder.age;
    }

    public static class Builder {
        private String userId;
        private String email;
        private String bio = "No bio provided"; // Default
        private int age = 0;

        // --- [INTERVIEW_MVP] (Fluent API Structure) ---
        public Builder setBio(String b) { this.bio = b; return this; }
        public Builder setAge(int a) { this.age = a; return this; }

        // --- [PRODUCTION_ENHANCEMENT] (Mandatory Field Enforcement) ---
        // Force the caller to provide non-negotiable data up-front
        public Builder(String mandatoryId, String mandatoryEmail) {
            this.userId = mandatoryId;
            this.email = mandatoryEmail;
        }

        /**
         * Validates and returns the final product.
         * @throws IllegalStateException if business invariants are violated.
         */
        public UserSDE2 build() {
            // [PRODUCTION_ENHANCEMENT]: Final cross-field validation
            if (userId == null || email == null) {
                throw new IllegalStateException("Atomic failure: Mandatory fields missing.");
            }
            if (age < 0) {
                throw new IllegalStateException("Validation failure: Age cannot be negative.");
            }
            return new UserSDE2(this);
        }
    }

    @Override
    public String toString() {
        return "User[" + userId + " | " + email + " | " + bio + "]";
    }

    public static void main(String[] args) {
        UserSDE2 user = new UserSDE2.Builder("USR-1", "dev@google.com")
                .setBio("Founding Engineer")
                .setAge(28)
                .build();
        System.out.println("✅ " + user);
    }
}

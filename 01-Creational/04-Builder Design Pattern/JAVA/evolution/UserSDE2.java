package evolution;

/**
 * <h1>Pragmatic SDE-2+ Builder: Mandatory vs. Optional Fields</h1>
 * 
 * A common problem with the basic Builder is that users can forget 
 * to call mandatory methods (like setFirstName). 
 * 
 * Solution: Pass mandatory fields into the Builder's constructor!
 */
public class UserSDE2 {

    private final String firstName; // Mandatory
    private final String lastName;  // Mandatory
    private final String email;     // Optional
    private final int age;          // Optional

    private UserSDE2(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.age = builder.age;
    }

    @Override
    public String toString() {
        return "UserSDE2 [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", age=" + age + "]";
    }

    public static class Builder {
        // Mandatory fields
        private final String firstName;
        private final String lastName;

        // Optional fields - initialized to defaults
        private String email = "unknown@example.com";
        private int age = 0;

        // 1. Mandatory fields go in the Builder Constructor!
        public Builder(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setAge(int age) {
            this.age = age;
            return this;
        }

        public UserSDE2 build() {
            // 2. VALIDATION: Ensure the object is in a consistent state
            if (firstName == null || lastName == null) {
                throw new IllegalStateException("Name cannot be null");
            }
            if (age < 0) {
                throw new IllegalStateException("Age cannot be negative");
            }
            
            return new UserSDE2(this);
        }
    }

    public static void main(String[] args) {
        // Mandatory fields must be provided up-front
        UserSDE2 user = new UserSDE2.Builder("John", "Doe")
                .setEmail("john.doe@gmail.com")
                .setAge(25)
                .build();
                
        System.out.println("✅ SDE-2 Builder (Mandatory vs Optional): " + user);
    }
}

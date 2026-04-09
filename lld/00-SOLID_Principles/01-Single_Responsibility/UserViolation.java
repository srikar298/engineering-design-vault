/**
 * ============================================================================
 * ☣️ VIOLATION: Single Responsibility Principle (SRP)
 * ============================================================================
 * 
 * DESCRIPTION:
 * This class is a "God Class" because it is sensitive to THREE independent
 * Stakeholders (Change Vectors):
 * 1. Finance/Business Team -> (Registration Logic & Pricing)
 * 2. Infra/DBA Team -> (How data is saved)
 * 3. Product/Security Team -> (Audit & Compliance Logging)
 * 
 * THE SENIOR INTERVIEW LENS:
 * Don't just say "it does three things." Say "it has three distinct stakeholders
 * who can request changes at different times and for different reasons."
 * 
 * If the Finance team changes registration rules, we shouldn't have to risk
 * breaking the Database logic.
 */
public class UserViolation {

    private String username;
    private String email;

    public UserViolation(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // --- Responsibility 1: Business Logic ---
    public void registerUser() {
        if (isValidUser()) {
            System.out.println("Processing registration for: " + username);
            
            // --- Responsibility 2: Persistence (Embedded Logic) ---
            saveToDatabase();
            
            // --- Responsibility 3: Logging (Embedded Logic) ---
            logActivity("User " + username + " registered successfully.");
        }
    }

    private boolean isValidUser() {
        return email.contains("@");
    }

    // VIOLATION: Database logic inside a User class
    private void saveToDatabase() {
        System.out.println("Saving User to Database...");
        // Imagine complex SQL or File I/O here
    }

    // VIOLATION: Logging logic inside a User class
    private void logActivity(String message) {
        System.out.println("[AUDIT LOG] " + message);
        // Imagine writing to a log file or a central logging server here
    }

    public static void main(String[] args) {
        UserViolation user = new UserViolation("Saavy", "saavy@example.com");
        user.registerUser();
    }
}

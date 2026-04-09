/**
 * ============================================================================
 * ✅ REFACTORED: Single Responsibility Principle (SRP)
 * ============================================================================
 * 
 * DESCRIPTION:
 * We have decoupled the God Class based on STAKEHOLDER boundaries.
 * 
 * 1. User: Data & Business rules (Owned by Finance/Product).
 * 2. UserRepository: Persistence (Owned by Infra/Platform).
 * 3. LoggerService: Compliance & Auditing (Owned by Security/Ops).
 * 
 * THE SENIOR INTERVIEW LENS:
 * "Each class now has a single change axis. If the DB schema changes, only
 * UserRepository is sensitive to that change. The business rules in 'User'
 * remain untouched and stable."
 */

// --- Component 1: Focused Data Model ---
class User {
    private String username;
    private String email;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public boolean isValid() {
        return email.contains("@");
    }

    public String getUsername() {
        return username;
    }
}

// --- Component 2: Focused Persistence Layer ---
class UserRepository {
    public void save(User user) {
        System.out.println("Saving " + user.getUsername() + " to Database...");
        // Only persistence logic lives here.
    }
}

// --- Component 3: Focused Logging Layer ---
class LoggerService {
    public void log(String message) {
        System.out.println("[AUDIT LOG] " + message);
        // Only logging logic lives here.
    }
}

// --- Orchestrator: Bringing it together ---
public class UserRefactored {

    private final UserRepository repository = new UserRepository();
    private final LoggerService logger = new LoggerService();

    public void registerUser(String name, String email) {
        User user = new User(name, email);

        if (user.isValid()) {
            repository.save(user);
            logger.log("User " + name + " registered successfully.");
        } else {
            logger.log("Registration failed for invalid user data.");
        }
    }

    public static void main(String[] args) {
        UserRefactored app = new UserRefactored();
        app.registerUser("Saavy", "saavy@example.com");
    }
}

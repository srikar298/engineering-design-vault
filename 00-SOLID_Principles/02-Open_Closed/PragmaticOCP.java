/**
 * ============================================================================
 * ⚖️ PRAGMATIC OCP: When NOT to Over-Engineer
 * ============================================================================
 * 
 * SCENARIO: 
 * A simple CRUD service for a User profile.
 * 
 * THE SENIOR INSIGHT:
 * OCP is an investment. If behavior is unlikely to vary or the system is 
 * short-lived, adding interfaces adds "Design Debt."
 */

// --- ✅ PRAGMATIC (Correct for 90% of CRUD cases) ---
class UserService {
    public void updateProfile(String userId, String email) {
        System.out.println("Updating user " + userId + " with email " + email);
        // Simple, readable, direct.
    }
}

// --- ❌ OVER-ENGINEERED (Design Theatre) ---
interface ProfileUpdateStrategy {
    void update(String userId, String email);
}

class StandardProfileUpdate implements ProfileUpdateStrategy {
    public void update(String userId, String email) {
        System.out.println("Updating user...");
    }
}

class OverEngineeredUserService {
    // Why? Will we ever have "TurboProfileUpdate" or "CryptoProfileUpdate"?
    // Unlikely. This is abstraction for the sake of abstraction.
}

public class PragmaticOCP {
    public static void main(String[] args) {
        System.out.println("Senior Engineers know when to KEEP IT SIMPLE.");
    }
}

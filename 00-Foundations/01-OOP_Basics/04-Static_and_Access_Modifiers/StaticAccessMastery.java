/**
 * ============================================================================
 * ⚡ STATIC & ACCESS MASTERY (Scope, Multi-tenancy, and Encapsulation)
 * ============================================================================
 */

// --- 1. STATIC members vs. INSTANCE members ---
class CloudService {
    // Static: Shared across ALL instances (Lives in Metaspace)
    public static final String REGION = "US-EAST-1"; 
    private static int totalActiveConnections = 0;

    // Instance: Unique to EACH instance (Lives in Heap)
    private String serviceId;
    private boolean isActive;

    public CloudService(String id) {
        this.serviceId = id;
        this.isActive = true;
        totalActiveConnections++; // Shared state update
    }

    public static int getActiveConnections() {
        // Can ONLY access static fields. Cannot use 'this' or instance fields.
        return totalActiveConnections;
    }

    public void status() {
        System.out.println("Service [" + serviceId + "] in " + REGION + " is " + (isActive ? "ACTIVE" : "OFFLINE"));
    }
}

// --- 2. THE 4 ACCESS MODIFIERS (Foundation of Encapsulation) ---
/**
 * SENIOR RULE: "The Default choice is PRIVATE."
 */
class SecureVault {
    // 1. private: Only accessible within this class (Maximum Security)
    private double currentBalance;

    // 2. default (package-private): Accessible only within the same package
    // Useful for 'internal' library components that shouldn't leak to users.
    String internalAuditCode = "AUDIT-999";

    // 3. protected: Accessible in same package + subclasses (even in other packages)
    // Useful for Template Methods or Framework extension points.
    protected String vaultType = "HEAVY-DUTY";

    // 4. public: Accessible anywhere (Open API)
    public String vaultOwner;

    public SecureVault(String owner, double initialDeposit) {
        this.vaultOwner = owner;
        this.currentBalance = initialDeposit;
    }

    // Public Behavior is the ONLY way to touch private state.
    public void withdraw(double amount) {
        if (amount <= currentBalance) {
            currentBalance -= amount;
            System.out.println("Withdrawal successful.");
        } else {
            System.out.println("Insufficient funds!");
        }
    }
}

public class Static_Access_Mastery {
    public static void main(String[] args) {
        System.out.println("=== ⚡ Static Member Analysis ===");
        CloudService s1 = new CloudService("EC2-001");
        CloudService s2 = new CloudService("S3-001");

        System.out.println("Global Region: " + CloudService.REGION); // Static access via Class
        System.out.println("Active Connections: " + CloudService.getActiveConnections()); 

        s1.status();
        s2.status();

        System.out.println("\n=== 🚦 Access Modifier Analysis ===");
        SecureVault myVault = new SecureVault("Alice", 1000.0);
        
        // Allowed: public field
        System.out.println("Vault Owner: " + myVault.vaultOwner);

        // Allowed: default & protected (because we are in the same package/file)
        System.out.println("Internal Code: " + myVault.internalAuditCode);
        System.out.println("Vault Type: " + myVault.vaultType);

        // BLOCKED: private field (uncomment to see error)
        // System.out.println(myVault.currentBalance); 

        // Correct way: public behavior
        myVault.withdraw(500);
    }
}

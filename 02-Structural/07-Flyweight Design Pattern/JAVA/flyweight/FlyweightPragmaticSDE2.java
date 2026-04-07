package flyweight;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * <h1>07 - Flyweight: The "Memory Shield" (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> A SaaS platform with 10,000 active <code>UserSession</code> objects. 
 * Each session needs a <code>PermissionTree</code> (metadata). If every session 
 * had its own 1MB tree, memory would hit 10GB.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Intrinsic vs Extrinsic:</b> Shared state (metadata) is <b>Intrinsic</b> and 
 *    immutable. Unique state (session token) is <b>Extrinsic</b> and passed in.
 * 2. <b>Concurrency:</b> In high-load systems, the Flyweight Factory MUST use 
 *    a thread-safe cache (ConcurrentHashMap).
 * 3. <b>GC Efficiency:</b> Fewer objects = less GC work = better 99th percentile latency.
 * 
 * <b>Edge Cases Handled:</b>
 * - <b>Race Conditions:</b> Thread-safe factory for 10k users.
 * - <b>RAM Exhaustion:</b> Objects are shared, keeping the heap flat.
 * - <b>Immutability:</b> Shared metadata cannot be changed once created.
 */

// --- FLYWEIGHT (Intrinsic - Shared & Immutable) ---
class RolePermissions {
    private final String role;
    private final String heavyMetadata; // Imagine 1MB resource

    public RolePermissions(String r) {
        this.role = r;
        this.heavyMetadata = "Permission_Tree_for_" + r;
        System.out.println("[Flyweight] Loading heavy resource for " + r);
    }

    public void validate(String token) {
        System.out.println("Session [" + token + "] validated using shared " + role + " metadata.");
    }
}

// --- FACTORY (The Controller) ---
class PermissionFactory {
    // [PRODUCTION_ENHANCEMENT]: Thread-safe cache for high-concurrency
    private static final Map<String, RolePermissions> cache = new ConcurrentHashMap<>();

    public static RolePermissions get(String role) {
        // [INTERVIEW_MVP]: Atomic lookup/creation
        return cache.computeIfAbsent(role, RolePermissions::new);
    }
}

// --- CONTEXT (Extrinsic - Unique) ---
class UserSession {
    private final String token; // Unique
    private final RolePermissions permissions; // Reference to Shared

    public UserSession(String t, String r) {
        this.token = t;
        this.permissions = PermissionFactory.get(roleNormalization(r));
    }

    private String roleNormalization(String r) { return r.toUpperCase(); }

    public void run() { permissions.validate(token); }
}

public class FlyweightPragmaticSDE2 {
    public static void main(String[] args) {
        UserSession s1 = new UserSession("TOK-1", "ADMIN");
        UserSession s2 = new UserSession("TOK-2", "GUEST");
        UserSession s3 = new UserSession("TOK-3", "ADMIN"); // Shared instance reused

        s1.run(); s2.run(); s3.run();
    }
}

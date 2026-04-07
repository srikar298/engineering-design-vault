package proxy;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>04 - Proxy: The "Object Controller" (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> A sensitive <code>Database</code> service. You need to ensure 
 * that only 'ADMIN' users can delete data, and you want to cache query results 
 * to save money on cloud SQL bills.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Lifecycle Control:</b> The Proxy creates/manages the RealSubject. 
 *    The client never touches the <code>RealDatabase</code> directly.
 * 2. <b>Lazy Initialization:</b> The real heavy object is only created if a 
 *    method is actually called (Virtual Proxy).
 * 3. <b>Aspect Oriented Programming (AOP):</b> This is how Spring handles 
 *    Transactions and Security stubs.
 * 
 * <b>Edge Cases Handled:</b>
 * - <b>Access Denied:</b> Throws SecurityException for invalid roles.
 * - <b>Cache Hit vs Miss:</b> Transparently returns cached data.
 * - <b>Resource Management:</b> Shields the RealSubject from unauthorized calls.
 */

interface Database { 
    String fetch(String id); 
    void delete(String id);
}

class RealDatabase implements Database {
    public RealDatabase() { System.out.println("SQL: Establishing heavy connection..."); }
    @Override public String fetch(String id) { return "Data_" + id; }
    @Override public void delete(String id) { System.out.println("SQL: Deleted " + id); }
}

class DatabaseProxy implements Database {
    private RealDatabase realDB; // Virtual Proxy (Lazy)
    private final Map<String, String> cache = new HashMap<>();
    private final String userRole;

    public DatabaseProxy(String role) { this.userRole = role; }

    private void ensureInitialized() {
        if (realDB == null) realDB = new RealDatabase();
    }

    @Override
    public String fetch(String id) {
        // --- [PRODUCTION_ENHANCEMENT] (Caching Proxy) ---
        if (cache.containsKey(id)) {
            System.out.println("Proxy: Cache Hit for " + id);
            return cache.get(id);
        }
        ensureInitialized();
        String res = realDB.fetch(id);
        cache.put(id, res);
        return res;
    }

    @Override
    public void delete(String id) {
        // --- [INTERVIEW_MVP] (Protection Proxy) ---
        if (!"ADMIN".equals(userRole)) {
            throw new SecurityException("Forbidden: Role " + userRole + " cannot delete.");
        }
        ensureInitialized();
        realDB.delete(id);
    }
}

public class ProxyPragmaticSDE2 {
    public static void main(String[] args) {
        Database db = new DatabaseProxy("GUEST");
        System.out.println(db.fetch("User_1")); // Establish connection + fetch
        System.out.println(db.fetch("User_1")); // Hit cache
        
        try {
            db.delete("User_1"); // Should fail
        } catch (Exception e) {
            System.err.println("✅ Security Guard working: " + e.getMessage());
        }
    }
}

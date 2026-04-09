package addons.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>12 - Resilient Distributed Cache Client (The "Decorator" Pattern)</h1>
 * 
 * <b>Scenario:</b> Interacting with a remote cache (like Redis) can fail due to 
 * network blips. A Senior SDE-2 doesn't just call the client; they wrap it 
 * with <b>Retries</b> and <b>Fallbacks</b> to the Database.
 * 
 * <b>Patterns Used:</b>
 * 1. <b>Decorator Pattern:</b> Layers functionality (Logging -> Metrics -> Retries) 
 *    on top of the base cache client.
 * 2. <b>Strategy Pattern:</b> Different serialization strategies (JSON, Protobuf).
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Fail-Fast vs Fail-Silent:</b> In caching, we usually fail-silent 
 *    (fallback to DB) to maintain Availability over Consistency (AP).
 * 2. <b>Serialization Overhead:</b> High-throughput systems should use Protobuf 
 *    instead of JSON to save CPU and Bandwidth.
 */

interface CacheClient {
    String get(String key);
    void set(String key, String value);
}

// --- BASE IMPLEMENTATION (Simulated Redis) ---
class RedisClient implements CacheClient {
    private final Map<String, String> store = new HashMap<>();
    
    @Override
    public String get(String key) {
        System.out.println("   [REDIS] GET " + key);
        return store.get(key);
    }

    @Override
    public void set(String key, String value) {
        System.out.println("   [REDIS] SET " + key);
        store.put(key, value);
    }
}

// --- DECORATOR: Network Retry Logic ---
class RetryingCacheDecorator implements CacheClient {
    private final CacheClient delegate;
    private static final int MAX_RETRIES = 2;

    public RetryingCacheDecorator(CacheClient delegate) { this.delegate = delegate; }

    @Override
    public String get(String key) {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            try {
                return delegate.get(key);
            } catch (Exception e) {
                attempts++;
                System.out.println("   [RETRY] Attempt " + attempts + " failed...");
            }
        }
        return null; // Fail-silent
    }

    @Override
    public void set(String key, String value) {
        delegate.set(key, value);
    }
}

// --- DECORATOR: Database Fallback ---
class FallbackCacheDecorator implements CacheClient {
    private final CacheClient delegate;

    public FallbackCacheDecorator(CacheClient delegate) { this.delegate = delegate; }

    @Override
    public String get(String key) {
        String res = delegate.get(key);
        if (res == null) {
            System.out.println("   [FALLBACK] Cache Miss/Failure. Fetching from Database...");
            return "DB_VALUE_FOR_" + key; // Simulation
        }
        return res;
    }

    @Override
    public void set(String key, String value) {
        delegate.set(key, value);
    }
}

public class ResilientCacheSDE2 {
    public static void main(String[] args) {
        // Build the pipeline: Redis -> Retry Logic -> DB Fallback
        CacheClient client = new FallbackCacheDecorator(
                                new RetryingCacheDecorator(
                                    new RedisClient()
                                )
                             );

        System.out.println("--- Scenario 1: Initial Fetch ---");
        System.out.println("Result: " + client.get("user:123"));

        System.out.println("\n--- Scenario 2: Save and Fetch ---");
        client.set("user:123", "Alice");
        System.out.println("Result: " + client.get("user:123"));
    }
}

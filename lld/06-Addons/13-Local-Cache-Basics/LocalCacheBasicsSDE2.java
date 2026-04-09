package addons.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * <h1>13 - Local Cache Basics (The "Foundation" Pattern)</h1>
 * 
 * <b>Scenario:</b> You need to store user permissions in memory. They rarely 
 * change, and the DB call is slow. 
 * 
 * <b>SDE-1 vs SDE-2 Thinking:</b>
 * - <b>SDE-1:</b> Uses a standard <code>HashMap</code> and hits a <code>ConcurrentModificationException</code> in production.
 * - <b>SDE-2:</b> Uses <code>ConcurrentHashMap</code> for high-concurrency and thread-safety.
 * 
 * <b>Key Concepts:</b>
 * 1. <b>Thread Safety:</b> How to allow multiple threads to read/write simultaneously.
 * 2. <b>ComputeIfAbsent:</b> An atomic way to "Check then Act" (Prevents Cache Stampede on a local level).
 */

public class LocalCacheBasicsSDE2 {

    // --- APPROACH A: The Naive Sync (Slow) ---
    // Entire map is locked for every read/write.
    private final Map<String, String> syncCache = Collections.synchronizedMap(new HashMap<>());

    // --- APPROACH B: The Production Standard (Fast) ---
    // Uses "Bucket-level" locking. Multiple reads can happen in parallel.
    private final ConcurrentHashMap<String, String> fastCache = new ConcurrentHashMap<>();

    public String getValue(String key) {
        // --- [INTERVIEW_MVP] (Atomic Check-and-Compute) ---
        // This ensures the DB is only called ONCE for this key, even if 10 threads call it.
        return fastCache.computeIfAbsent(key, k -> fetchFromDatabase(k));
    }

    private String fetchFromDatabase(String key) {
        System.out.println("   [DB] Slow call for: " + key);
        return "Data_for_" + key;
    }

    public static void main(String[] args) {
        LocalCacheBasicsSDE2 cache = new LocalCacheBasicsSDE2();

        System.out.println("1st call: " + cache.getValue("user:1")); // Hits DB
        System.out.println("2nd call: " + cache.getValue("user:1")); // Hits Cache
        
        // Parallel access simulation
        Runnable task = () -> cache.getValue("user:parallel");
        new Thread(task).start();
        new Thread(task).start(); // computeIfAbsent ensures only 1 DB call happens
    }
}

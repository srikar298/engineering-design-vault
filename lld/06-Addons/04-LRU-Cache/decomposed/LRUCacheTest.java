package addons.cache.decomposed;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Verification test suite for LRUCache verifying functionality and concurrency safety.
 */
public class LRUCacheTest {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting LRUCache verification tests...");
        
        testBasicOperations();
        System.out.println("Basic operations test passed.");

        testEviction();
        System.out.println("Eviction test passed.");

        testConcurrency();
        System.out.println("Concurrency test passed.");

        System.out.println("\nSUCCESS: All LRUCache tests passed!");
    }

    private static void testBasicOperations() {
        Cache<String, Integer> cache = new LRUCache<>(3);
        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);

        if (cache.size() != 3) {
            throw new AssertionError("Expected size 3, got " + cache.size());
        }
        if (cache.get("A") != 1) {
            throw new AssertionError("Expected get(A) to be 1");
        }
        if (cache.get("B") != 2) {
            throw new AssertionError("Expected get(B) to be 2");
        }
        if (cache.get("C") != 3) {
            throw new AssertionError("Expected get(C) to be 3");
        }
        
        cache.put("A", 10);
        if (cache.get("A") != 10) {
            throw new AssertionError("Expected get(A) to be updated to 10");
        }
        if (cache.size() != 3) {
            throw new AssertionError("Expected size to remain 3");
        }
    }

    private static void testEviction() {
        Cache<String, Integer> cache = new LRUCache<>(2);
        cache.put("A", 1);
        cache.put("B", 2);
        
        // Access A to make it most recently used, B becomes least recently used (LRU)
        cache.get("A");
        
        // Put C, which triggers eviction of the LRU element (B)
        cache.put("C", 3);
        
        if (cache.get("B") != null) {
            throw new AssertionError("Expected B to be evicted");
        }
        if (cache.get("A") != 1) {
            throw new AssertionError("Expected A to remain in cache");
        }
        if (cache.get("C") != 3) {
            throw new AssertionError("Expected C to remain in cache");
        }
        if (cache.size() != 2) {
            throw new AssertionError("Expected size to be 2");
        }
    }

    private static void testConcurrency() throws InterruptedException {
        int threadsCount = 12;
        int operationsPerThread = 10000;
        Cache<String, Integer> cache = new LRUCache<>(100);
        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);
        
        AtomicInteger hitCount = new AtomicInteger(0);
        AtomicInteger missCount = new AtomicInteger(0);

        for (int i = 0; i < threadsCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    // Using modulo 97 (prime) ensures puts and gets overlap, producing hits
                    String key = "key_" + (j % 97);
                    if (j % 3 == 0) {
                        cache.put(key, threadId * 10000 + j);
                    } else {
                        Integer val = cache.get(key);
                        if (val != null) {
                            hitCount.incrementAndGet();
                        } else {
                            missCount.incrementAndGet();
                        }
                    }
                }
            });
        }

        executor.shutdown();
        boolean finished = executor.awaitTermination(15, TimeUnit.SECONDS);
        if (!finished) {
            throw new AssertionError("Executor did not terminate in time under concurrency");
        }
        
        System.out.println("  Concurrency Metrics -> Hits: " + hitCount.get() + ", Misses: " + missCount.get());
        if (cache.size() > 100) {
            throw new AssertionError("Cache size exceeded capacity under concurrency: " + cache.size());
        }
        if (hitCount.get() == 0) {
            throw new AssertionError("Concurrency test failed: 0 hits recorded");
        }
    }
}

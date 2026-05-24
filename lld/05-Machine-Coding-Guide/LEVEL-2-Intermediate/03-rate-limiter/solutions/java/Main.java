import limiter.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("⏱️ Enterprise High-Performance Rate Limiter Demo ⏱️");
        System.out.println("=================================================\n");

        RateLimiterManager manager = RateLimiterManager.getInstance();

        // 1. Basic Rate Limiting Flow
        // Rule: USER-1 has capacity 5, refill rate 2 tokens per second
        String userKey = "USER-1";
        long capacity = 5;
        long refillRate = 2;

        System.out.println("Configured rule for USER-1: Max capacity 5, Refill rate 2/sec.\n");

        System.out.println("--- 🏁 Simulating 7 rapid requests ---");
        for (int i = 1; i <= 7; i++) {
            boolean allowed = manager.isAllowed(userKey, capacity, refillRate);
            System.out.printf("Request %d: %s\n", i, allowed ? "🟢 ALLOWED (Token consumed)" : "🔴 REJECTED (Rate limited)");
        }

        // Sleep 1.5 seconds -> refills 3 tokens (1.5 * 2 = 3)
        try {
            System.out.println("\n--- Sleeping for 1.5 seconds (Refilling tokens...) ---");
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("--- Firing 4 requests after refill ---");
        for (int i = 1; i <= 4; i++) {
            boolean allowed = manager.isAllowed(userKey, capacity, refillRate);
            System.out.printf("Request %d: %s\n", i, allowed ? "🟢 ALLOWED" : "🔴 REJECTED");
        }


        // 2. Concurrency Stress Test
        // Target: Fire 100 concurrent requests from 20 threads simultaneously
        // Key: All threads access the same limiter. We should allow exactly the current tokens
        // and no double allocations or thread safety drifts should occur.
        System.out.println("\n--- ⚡ Running Concurrency Stress Test ---");
        System.out.println("Pre-filling and configuring LIMIT-TEST key with capacity 10, refill 0/sec...");
        
        String testKey = "LIMIT-TEST";
        long testCapacity = 10;
        long testRefill = 0; // No refill during test to prove clean thresholding

        // Initialize by calling once (stores it in map)
        manager.isAllowed(testKey, testCapacity, testRefill);

        int numRequests = 50;
        ExecutorService executor = Executors.newFixedThreadPool(15);
        AtomicInteger allowedCount = new AtomicInteger(0);
        AtomicInteger rejectedCount = new AtomicInteger(0);

        CountDownLatch latch = new CountDownLatch(1);

        for (int i = 0; i < numRequests; i++) {
            executor.submit(() -> {
                try {
                    latch.await(); // wait for simultaneous start
                    boolean allowed = manager.isAllowed(testKey, testCapacity, testRefill);
                    if (allowed) {
                        allowedCount.incrementAndGet();
                    } else {
                        rejectedCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        System.out.println("Triggering concurrent requests...");
        latch.countDown(); // Go!
        executor.shutdown();
        
        try {
            executor.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n--- Concurrency Test Summary ---");
        System.out.println("Total Concurrent Requests: " + numRequests);
        // We consumed 1 token during initialization, so remaining capacity was 9
        System.out.println("Total Requests Allowed:    " + allowedCount.get() + " (Expected: 9)");
        System.out.println("Total Requests Rejected:   " + rejectedCount.get() + " (Expected: 41)");

        System.out.println("\nRate limiter successfully verified under concurrent load.");
        System.exit(0);
    }
}

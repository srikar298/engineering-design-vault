package addons.lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

/**
 * <h1>02 - Distributed Lock Simulator (The "Fault-Tolerant" Resource Protector)</h1>
 * 
 * <b>Scenario:</b> You are building a <b>Distributed Ticket Booking System</b>. 
 * Multiple nodes (JVMs) are trying to book the same seat. You need a lock that 
 * is cross-node (simulated here) and handles crashes.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Lease Expiration:</b> A lock must have a TTL (Time-To-Live). If a worker 
 *    crashes, the lock MUST automatically release after the TTL to prevent deadlocks.
 * 2. <b>Heartbeat (Renewal):</b> If a worker is still processing a long task, 
 *    it must "renew" the lease before it expires.
 * 3. <b>Fencing Tokens:</b> (Advanced) Use monotonically increasing version 
 *    numbers to reject late-arriving requests from "zombie" workers.
 */

class LockMetadata {
    public final String ownerId;
    public long expirationTime;

    public LockMetadata(String ownerId, long ttlMs) {
        this.ownerId = ownerId;
        this.expirationTime = System.currentTimeMillis() + ttlMs;
    }

    public void renew(long ttlMs) {
        this.expirationTime = System.currentTimeMillis() + ttlMs;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTime;
    }
}

class DistributedLockManager {
    // Simulated Centralized Store (Redis/Etcd)
    private final ConcurrentHashMap<String, LockMetadata> store = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public boolean acquireLock(String lockKey, String ownerId, long ttlMs) {
        LockMetadata current = store.get(lockKey);
        
        // If lock exists but is expired, remove it (simulated cleanup)
        if (current != null && current.isExpired()) {
            store.remove(lockKey, current);
            current = null;
        }

        // Try to put the lock metadata (Atomically)
        if (store.putIfAbsent(lockKey, new LockMetadata(ownerId, ttlMs)) == null) {
            System.out.println("[LOCK] Node " + ownerId + " acquired '" + lockKey + "'");
            return true;
        }
        
        return false;
    }

    public void releaseLock(String lockKey, String ownerId) {
        LockMetadata current = store.get(lockKey);
        if (current != null && current.ownerId.equals(ownerId)) {
            store.remove(lockKey);
            System.out.println("[LOCK] Node " + ownerId + " released '" + lockKey + "'");
        }
    }

    public void renewLease(String lockKey, String ownerId, long ttlMs) {
        LockMetadata current = store.get(lockKey);
        if (current != null && current.ownerId.equals(ownerId)) {
            current.renew(ttlMs);
            System.out.println("[LOCK] Node " + ownerId + " RENEWED '" + lockKey + "'");
        }
    }
}

public class DistributedLockSimulatorSDE2 {
    public static void main(String[] args) throws InterruptedException {
        DistributedLockManager manager = new DistributedLockManager();
        String seatId = "SEAT_A1";
        String nodeA = "NODE_ALPHA";
        String nodeB = "NODE_BETA";

        // 1. Node A acquires lock for 2 seconds
        if (manager.acquireLock(seatId, nodeA, 2000)) {
            // Simulate work...
            Thread.sleep(1000);
            
            // 2. Node A renews the lease
            manager.renewLease(seatId, nodeA, 2000);
            
            // 3. Node B tries to acquire (Should FAIL)
            boolean bSuccess = manager.acquireLock(seatId, nodeB, 2000);
            System.out.println("Node B acquired? " + bSuccess); // false

            Thread.sleep(2500); // Node A finishes work, but let's say it "crashes" here
        }

        // 4. Node B tries again after A's lease expires
        boolean bRetrySuccess = manager.acquireLock(seatId, nodeB, 2000);
        System.out.println("Node B acquired after expiration? " + bRetrySuccess); // true
    }
}

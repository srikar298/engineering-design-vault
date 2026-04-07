package database;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

/**
 * <h1>InventoryDatabase v2 — Production-Grade Read-Write Locking</h1>
 *
 * <p>Upgrades over v1:
 * <ol>
 *   <li><strong>tryLock with timeout:</strong> Readers/writers wait at most
 *       {@link #LOCK_TIMEOUT_MS}ms instead of blocking forever. If a thread
 *       cannot acquire the lock, it returns instantly with a fallback value
 *       rather than hanging the calling HTTP handler.</li>
 *
 *   <li><strong>Write-fairness flag:</strong>
 *       {@code new ReentrantReadWriteLock(true)} enables fair queuing so
 *       waiting writers are not indefinitely starved by an endless stream
 *       of arriving readers. Without fairness, under constant read traffic
 *       the write lock might never run.</li>
 *
 *   <li><strong>Optimistic Read (StampedLock):</strong> A secondary cache
 *       demonstrates the Optimistic Read pattern — read without locking,
 *       then validate. If data changed during the read, fall back to a
 *       real read lock. This gives maximum throughput on read-heavy paths
 *       where stock rarely changes.</li>
 *
 *   <li><strong>Lock degradation (write → read):</strong> After a write,
 *       the lock is degraded to a read lock before releasing write so the
 *       writing thread can also read the value it just wrote without
 *       another thread sneaking in a second write between release and re-acquire.</li>
 * </ol>
 *
 * <h2>Real-World Mapping</h2>
 * <ul>
 *   <li>PostgreSQL: Shared Lock (read) vs Exclusive Lock (write) on rows</li>
 *   <li>Java's ConcurrentHashMap uses segment-level read-write locks internally</li>
 *   <li>Redis: single-threaded, so naturally avoids this problem entirely</li>
 * </ul>
 */
public class InventoryDatabase {

    private static final long LOCK_TIMEOUT_MS = 200; // max wait for any lock

    // volatile guarantees visibility across CPU cores even when read outside the lock
    private volatile int stockCount = 100;

    /**
     * Fair mode (true) = writers queue behind readers. No writer starvation.
     * Unfair mode (false) = writers can barge in but may starve. Higher throughput.
     * Production choice depends on write frequency.
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true); // fair

    /**
     * StampedLock for optimistic reads — zero locking overhead on the fast path.
     * Falls back to a real read lock only if data changed during the read.
     */
    private final StampedLock stampedLock = new StampedLock();
    private volatile int cachedStock = 100; // separate cache for optimistic demo

    // ---------------------------------------------------------------
    // Standard Read with tryLock timeout (prevents infinite block)
    // ---------------------------------------------------------------

    public int readStock() {
        try {
            boolean acquired = lock.readLock().tryLock(LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (!acquired) {
                System.out.println("   ⏱️  [" + Thread.currentThread().getName()
                                   + "] Could not acquire read lock in time. Returning stale value.");
                return stockCount; // serve stale value rather than hang
            }
            try {
                System.out.println("   📖 [" + Thread.currentThread().getName()
                                   + "] Reading stock: " + stockCount);
                Thread.sleep(300); // simulate slower read query
                return stockCount;
            } finally {
                lock.readLock().unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        }
    }

    // ---------------------------------------------------------------
    // Write with lock degradation (write → read) after update
    // ---------------------------------------------------------------

    public int updateStock(int newAmount) {
        try {
            boolean acquired = lock.writeLock().tryLock(LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (!acquired) {
                System.out.println("   ⏱️  [" + Thread.currentThread().getName()
                                   + "] Could not acquire write lock. Write skipped.");
                return stockCount;
            }
            try {
                System.out.println("   📝 [" + Thread.currentThread().getName()
                                   + "] WRITING new stock: " + newAmount);
                Thread.sleep(800);
                this.stockCount = newAmount;
                System.out.println("   ✅ Write completed. Stock = " + stockCount);

                // Lock DEGRADATION: acquire read lock before releasing write lock.
                // This guarantees THIS thread can read back the value it just wrote
                // without another writer sneaking in between unlock-write and lock-read.
                lock.readLock().lock();
            } finally {
                lock.writeLock().unlock(); // release write — other readers can now enter
            }

            // Now holding only read lock
            try {
                System.out.println("   🔽 [Lock Degraded → Read] Confirming written value: " + stockCount);
                return stockCount;
            } finally {
                lock.readLock().unlock();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        }
    }

    // ---------------------------------------------------------------
    // Optimistic Read (StampedLock) — zero contention fast path
    // ---------------------------------------------------------------

    public int readStockOptimistic() {
        long stamp = stampedLock.tryOptimisticRead(); // no locking!
        int value  = cachedStock;

        if (!stampedLock.validate(stamp)) {
            // Data changed during our read — fall back to a real read lock
            System.out.println("   🔁 [Optimistic] Conflict detected. Falling back to read lock.");
            stamp = stampedLock.readLock();
            try {
                value = cachedStock;
            } finally {
                stampedLock.unlockRead(stamp);
            }
        } else {
            System.out.println("   ⚡ [Optimistic] Clean read — no lock needed! Stock=" + value);
        }
        return value;
    }

    public void updateStockOptimistic(int newAmount) {
        long stamp = stampedLock.writeLock();
        try {
            System.out.println("   📝 [Optimistic] Updating cached stock to: " + newAmount);
            this.cachedStock = newAmount;
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }
}

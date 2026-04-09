import database.InventoryDatabase;
import workers.ReaderTask;
import workers.WriterTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <h1>Read-Write Lock v2 — Production-Grade Demo</h1>
 *
 * <p>Three scenarios:
 * <ol>
 *   <li>Standard fair read-write contention with lock timeout</li>
 *   <li>Lock degradation — writer confirms its own write without releasing</li>
 *   <li>Optimistic read (StampedLock) — zero-contention fast path</li>
 * </ol>
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("══════════════════════════════════════════════════════");
        System.out.println("   Read-Write Lock v2: Production-Grade Demo          ");
        System.out.println("   ✓ Fair mode  ✓ tryLock  ✓ Degradation  ✓ StampedLock");
        System.out.println("══════════════════════════════════════════════════════\n");

        InventoryDatabase db = new InventoryDatabase();

        // ----------------------------------------------------------------
        // SCENARIO 1: Fair RW contention — writers queue behind readers
        // ----------------------------------------------------------------
        System.out.println("══ Scenario 1: Fair Read-Write Contention ══\n");
        ExecutorService pool = Executors.newFixedThreadPool(6);

        // 3 readers fire simultaneously
        for (int i = 1; i <= 3; i++) pool.execute(new ReaderTask(db));
        Thread.sleep(50); // ensure readers are active

        // 1 writer — in fair mode it queues behind active readers
        pool.execute(new WriterTask(db, 250));

        // 2 more readers — in fair mode they queue BEHIND the waiting writer
        // (prevents write starvation)
        pool.execute(new ReaderTask(db));
        pool.execute(new ReaderTask(db));

        pool.shutdown();
        pool.awaitTermination(8, TimeUnit.SECONDS);

        // ----------------------------------------------------------------
        // SCENARIO 2: Lock Degradation — write then read atomically
        // ----------------------------------------------------------------
        System.out.println("\n══ Scenario 2: Lock Degradation (Write → Read) ══\n");
        db.updateStock(999); // internally degrades write lock to read lock

        // ----------------------------------------------------------------
        // SCENARIO 3: Optimistic Read (StampedLock)
        // ----------------------------------------------------------------
        System.out.println("\n══ Scenario 3: Optimistic Read (StampedLock) ══\n");

        System.out.println("First read — no concurrent write, clean stamp:");
        db.readStockOptimistic();

        System.out.println("\nSecond read — simulate concurrent write mid-read:");
        // We do the write first to cause stamp invalidation on next validate
        db.updateStockOptimistic(500);
        db.readStockOptimistic();

        System.out.println("\n✅ All scenarios complete.");
    }
}

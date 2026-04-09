import pool.MonitoredThreadPool;
import task.RequestTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * <h1>Thread Pool v2 — Production-Grade Demo</h1>
 *
 * <p>Demonstrates all five production fixes over v1:
 * <ol>
 *   <li>No busy-wait shutdown (awaitTermination replaces spin loop)</li>
 *   <li>Callable + Future = result visibility + exception propagation</li>
 *   <li>Per-task timeout (bounded execution time, prevents runaway tasks)</li>
 *   <li>Bounded queue + rejection handler (prevents OutOfMemoryError)</li>
 *   <li>Real-time pool metrics (active threads, queue depth, total time)</li>
 * </ol>
 */
public class Main {

    // Timeout for any single task — if exceeded the task is cancelled
    private static final long TASK_TIMEOUT_SECONDS = 3;

    public static void main(String[] args) throws InterruptedException {

        System.out.println("══════════════════════════════════════════════════════");
        System.out.println("   Thread Pool v2: Production-Grade Demo              ");
        System.out.println("   ✓ Callable  ✓ Future  ✓ Timeout  ✓ Rejection      ");
        System.out.println("══════════════════════════════════════════════════════\n");

        // 3 core threads, 3 max threads, queue capacity capped at 4
        // This means: 3 threads + 4 queued = max 7 in-flight. Task #8+ is rejected.
        MonitoredThreadPool pool = new MonitoredThreadPool(3, 3, 4);

        // ----------------------------------------------------------------
        // SCENARIO 1: Normal requests — all succeed with results
        // ----------------------------------------------------------------
        System.out.println("══ Scenario 1: Normal Workload (5 tasks) ══\n");
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            RequestTask task = new RequestTask("Request-" + i, 500);
            Future<String> future = pool.submit(task);
            futures.add(future);
        }

        // Collect results with individual timeouts
        for (Future<String> future : futures) {
            try {
                String result = future.get(TASK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                System.out.println("   📨 Result: " + result);
            } catch (TimeoutException e) {
                System.err.println("   ⏱️  Task timed out! Cancelling...");
                future.cancel(true); // interrupt the thread
            } catch (Exception e) {
                System.err.println("   ❌ Task failed: " + e.getMessage());
            }
        }

        pool.printMetrics();

        // ----------------------------------------------------------------
        // SCENARIO 2: Runaway task — task takes too long, gets cancelled
        // ----------------------------------------------------------------
        System.out.println("\n══ Scenario 2: Runaway Task (30s timeout) ══\n");

        RequestTask slowTask = new RequestTask("SlowRequest-DB-Leak", 30_000); // 30 seconds!
        Future<String> slowFuture = pool.submit(slowTask);

        System.out.println("   ⏳ Waiting up to " + TASK_TIMEOUT_SECONDS + "s for slow task...");
        try {
            slowFuture.get(TASK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            System.err.println("   ⏱️  Runaway task exceeded timeout! Cancelling to free the thread.");
            slowFuture.cancel(true);
        } catch (Exception e) {
            System.err.println("   ❌ Task failed: " + e.getMessage());
        }

        // ----------------------------------------------------------------
        // SCENARIO 3: Queue overflow — rejection policy fires
        // ----------------------------------------------------------------
        System.out.println("\n══ Scenario 3: Queue Overflow (8 tasks flood 3 threads + 4 queue) ══\n");

        for (int i = 1; i <= 8; i++) {
            RequestTask task = new RequestTask("FloodRequest-" + i, 1000);
            pool.submit(task); // task 8 will hit the rejection handler
        }

        // ----------------------------------------------------------------
        // Graceful shutdown — NO busy-wait spin loop
        // ----------------------------------------------------------------
        pool.shutdown();
        boolean finished = pool.awaitTermination(10, TimeUnit.SECONDS);
        if (!finished) {
            System.err.println("Pool did not finish in time. Force shutting down.");
            pool.shutdownNow();
        }

        pool.printMetrics();
        System.out.println("\n✅ Server shut down cleanly.");
    }
}

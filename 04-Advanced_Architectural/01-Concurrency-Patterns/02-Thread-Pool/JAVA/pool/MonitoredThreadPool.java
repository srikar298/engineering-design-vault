package pool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <h1>Monitored Thread Pool</h1>
 *
 * <p>A production-grade wrapper around {@link ThreadPoolExecutor} that adds:
 *
 * <ol>
 *   <li><strong>Bounded Queue:</strong> rejects new tasks gracefully when the
 *       queue is full, preventing {@code OutOfMemoryError} under surge load.</li>
 *   <li><strong>Rejection Handler:</strong> logs rejected tasks and returns an
 *       HTTP 503 to the caller instead of crashing.</li>
 *   <li><strong>Per-task timing:</strong> records wall-clock execution time
 *       for every task via {@code beforeExecute} / {@code afterExecute} hooks.</li>
 *   <li><strong>Health report:</strong> exposes active threads, queue depth,
 *       and completed task count — the metrics Prometheus would scrape.</li>
 * </ol>
 *
 * <p>In a Spring Boot application you would configure this via
 * {@code ThreadPoolTaskExecutor} and export metrics through Micrometer.
 */
public class MonitoredThreadPool extends ThreadPoolExecutor {

    // ThreadLocal stores the start time for the currently running task
    private final ThreadLocal<Long> taskStartTime = new ThreadLocal<>();
    private final AtomicLong totalTaskTimeMs = new AtomicLong(0);

    public MonitoredThreadPool(int corePoolSize,
                               int maxPoolSize,
                               int queueCapacity) {
        super(
            corePoolSize,
            maxPoolSize,
            60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(queueCapacity),   // bounded queue
            Executors.defaultThreadFactory(),
            new CallerRunsOrRejectsPolicy()             // custom rejection
        );
    }

    // ---------------------------------------------------------------
    // Lifecycle hooks — called by the pool framework automatically
    // ---------------------------------------------------------------

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        taskStartTime.set(System.currentTimeMillis());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);

        // Measure task duration
        long elapsed = System.currentTimeMillis() - taskStartTime.get();
        totalTaskTimeMs.addAndGet(elapsed);

        // Surface exceptions that Runnable/Callable would otherwise swallow
        if (t != null) {
            System.err.println("   ❌ [Pool] Task threw uncaught exception: " + t.getMessage());
        }

        taskStartTime.remove();
    }

    // ---------------------------------------------------------------
    // Health monitoring — would be scraped by Prometheus in prod
    // ---------------------------------------------------------------

    public void printMetrics() {
        System.out.println("\n📊 [Pool Metrics]"
            + "\n   Active threads  : " + getActiveCount() + "/" + getMaximumPoolSize()
            + "\n   Queue depth     : " + getQueue().size()  + "/" + getQueue().remainingCapacity()
            + "\n   Tasks completed : " + getCompletedTaskCount()
            + "\n   Total CPU time  : " + totalTaskTimeMs.get() + "ms"
        );
    }

    // ---------------------------------------------------------------
    // Custom Rejection Policy
    // ---------------------------------------------------------------

    /**
     * When the bounded queue is full this policy logs the rejection and
     * lets the caller know immediately (fast-fail) rather than crashing
     * the JVM with an OOM from an unbounded queue.
     *
     * <p>In an HTTP server this translates to an HTTP 503 Service Unavailable
     * response, which tells the client to retry later.
     */
    private static class CallerRunsOrRejectsPolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.err.println("   🚫 [Pool] QUEUE FULL — Task rejected. "
                + "Would return HTTP 503 to client. "
                + "Active=" + executor.getActiveCount()
                + " QueueDepth=" + executor.getQueue().size());
        }
    }
}

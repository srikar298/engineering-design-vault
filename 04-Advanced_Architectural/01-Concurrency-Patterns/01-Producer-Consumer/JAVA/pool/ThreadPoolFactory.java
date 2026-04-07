package pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <h1>Thread Pool Factory — CPU-bound vs I/O-bound Sizing</h1>
 *
 * <p>Hardcoding thread counts is an anti-pattern. The correct size depends
 * entirely on the type of work the threads perform.
 *
 * <h2>Why the formulas differ</h2>
 *
 * <h3>CPU-Bound Tasks (e.g. image resizing, encryption, JSON parsing)</h3>
 * <p>These tasks keep the CPU 100% busy the whole time. Having more threads
 * than CPU cores means the OS wastes time context-switching between threads
 * instead of doing real work. The +1 covers one thread being preempted.
 * <pre>
 *   Threads = N_cores + 1
 * </pre>
 *
 * <h3>I/O-Bound Tasks (e.g. DB query, external HTTP call, reading a file)</h3>
 * <p>While a thread waits for the database to respond it is doing nothing.
 * Other threads can (and should) use the CPU during that idle wait time.
 * The higher the wait ratio, the more threads you can run before they
 * start interfering with each other.
 * <pre>
 *   Threads = N_cores × (1 + Wait_time / Service_time)
 *
 *   Example: 4 cores, 2s wait, 0.5s CPU → 4 × (1 + 2/0.5) = 4 × 5 = 20
 * </pre>
 *
 * <p><strong>Interview Tip:</strong> Mention Little's Law — at steady state,
 * {@code L = λ × W} — which gives the same result from a queuing-theory
 * perspective.
 */
public class ThreadPoolFactory {

    private static final int CPU_CORES = Runtime.getRuntime().availableProcessors();

    /**
     * Creates a pool sized for <strong>CPU-bound</strong> work.
     * Threads = N_cores + 1
     */
    public static ExecutorService cpuBoundPool() {
        int size = CPU_CORES + 1;
        System.out.println("   🖥️  [Pool] CPU-bound pool created: " + size
                           + " threads (cores=" + CPU_CORES + ")");
        return Executors.newFixedThreadPool(size);
    }

    /**
     * Creates a pool sized for <strong>I/O-bound</strong> work.
     *
     * @param waitTimeMs     estimated blocking time per task (e.g. DB round-trip)
     * @param serviceTimeMs  estimated CPU time per task (actual computation)
     */
    public static ExecutorService ioBoundPool(long waitTimeMs, long serviceTimeMs) {
        double blockingCoefficient = (double) waitTimeMs / serviceTimeMs;
        int size = (int) Math.ceil(CPU_CORES * (1 + blockingCoefficient));
        System.out.println("   🌐 [Pool] I/O-bound pool created: " + size
                           + " threads (cores=" + CPU_CORES
                           + ", waitMs=" + waitTimeMs
                           + ", cpuMs=" + serviceTimeMs + ")");
        return Executors.newFixedThreadPool(size);
    }
}

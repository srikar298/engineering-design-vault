package workers;

import model.Message;
import queue.MessageQueue;

/**
 * <h1>CPU-Bound Task</h1>
 *
 * <p>Simulates pure computation — the CPU never waits on anything external.
 * Examples: image resizing, video transcoding, password hashing (bcrypt),
 * JSON serialisation, compression.
 *
 * <p>For tasks like these the optimal thread count is:
 * <pre>
 *   Threads = N_cores + 1
 * </pre>
 * Adding more threads only creates context-switch overhead that
 * <em>reduces</em> throughput.
 */
public class CpuBoundTask implements Runnable {

    private final MessageQueue queue;
    private final int id;

    public CpuBoundTask(int id, MessageQueue queue) {
        this.id    = id;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Message message = queue.consume();
                System.out.println("   🖥️  [CPU Worker-" + id + "] Hashing payload: "
                                   + message.getPayload());

                // Simulate CPU-heavy work (e.g. bcrypt rounds)
                long hash = computeIntensiveHash(message.getPayload());
                System.out.println("   🖥️  [CPU Worker-" + id + "] Result hash: " + hash);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Burns CPU cycles — no I/O, no sleeping. */
    private long computeIntensiveHash(String input) {
        long result = 0;
        for (int i = 0; i < 500_000; i++) {
            result = (result * 31 + input.hashCode()) % Long.MAX_VALUE;
        }
        return result;
    }
}

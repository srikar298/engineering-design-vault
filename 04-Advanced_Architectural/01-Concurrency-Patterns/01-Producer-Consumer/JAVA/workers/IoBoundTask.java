package workers;

import model.Message;
import queue.MessageQueue;

/**
 * <h1>I/O-Bound Task</h1>
 *
 * <p>Simulates work that blocks on an external resource: a database query,
 * an HTTP call to a payment gateway, reading from disk, or waiting for a
 * DNS resolution.
 *
 * <p>During the wait the CPU is completely idle. Other threads can (and
 * must) use the CPU during that idle period. This is why the optimal
 * thread count is much higher:
 *
 * <pre>
 *   Threads = N_cores × (1 + Wait_time / Service_time)
 *
 *   Example (4 cores, 200ms wait, 50ms CPU):
 *   Threads = 4 × (1 + 200/50) = 4 × 5 = 20
 * </pre>
 *
 * <p>With only 4 threads (the CPU-bound formula) 16 threads worth of
 * potential work would be wasted every second because threads sit idle
 * waiting for the database response.
 */
public class IoBoundTask implements Runnable {

    private final MessageQueue queue;
    private final int id;

    public IoBoundTask(int id, MessageQueue queue) {
        this.id    = id;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Message message = queue.consume();
                System.out.println("   🌐 [I/O Worker-" + id + "] Querying DB for: "
                                   + message.getPayload());

                // Simulate blocking I/O wait (e.g. network round-trip to DB = 200ms)
                Thread.sleep(200);

                // Simulate CPU work after the I/O returns (e.g. ORM mapping = 50ms)
                Thread.sleep(50);

                System.out.println("   🌐 [I/O Worker-" + id + "] DB response received for: "
                                   + message.getPayload());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

package queue;

import model.Message;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * <h1>Enhanced Message Queue</h1>
 *
 * <p>Upgraded from the v1 raw-String version with two production fixes:
 *
 * <ol>
 *   <li><strong>DLQ Routing:</strong> Messages that fail N retries are
 *       forwarded to the {@link DeadLetterQueue} instead of being silently
 *       dropped.</li>
 *   <li><strong>Health Monitoring:</strong> {@link #healthCheck()} exposes
 *       the current queue depth as a percentage. In production this would be
 *       exported to Prometheus/Grafana and trigger an auto-scale event when
 *       depth &gt; 80%.</li>
 * </ol>
 */
public class MessageQueue {

    private final BlockingQueue<Message> queue;
    private final int capacity;
    private final DeadLetterQueue dlq;

    private static final int WARNING_THRESHOLD_PERCENT = 80;

    public MessageQueue(int capacity, DeadLetterQueue dlq) {
        this.capacity = capacity;
        this.queue    = new ArrayBlockingQueue<>(capacity);
        this.dlq      = dlq;
    }

    // ---------------------------------------------------------------
    // Producer side
    // ---------------------------------------------------------------

    public void produce(Message message) throws InterruptedException {
        queue.put(message);   // blocks if queue is full (backpressure)
        healthCheck();
        System.out.println("📦 [Producer] Enqueued: " + message
                           + " | Depth: " + queue.size() + "/" + capacity);
    }

    // ---------------------------------------------------------------
    // Consumer side
    // ---------------------------------------------------------------

    public Message consume() throws InterruptedException {
        return queue.take();  // blocks if queue is empty
    }

    /** Route a message that exceeded max retries to the DLQ. */
    public void sendToDLQ(Message message) {
        dlq.route(message);
    }

    // ---------------------------------------------------------------
    // Health monitoring
    // ---------------------------------------------------------------

    /**
     * In production this metric is exported to Prometheus.
     * An alert fires when depth > 80%, triggering a Kubernetes HPA
     * scale-out of Consumer pods.
     */
    public void healthCheck() {
        int depthPercent = (int) ((double) queue.size() / capacity * 100);
        if (depthPercent >= WARNING_THRESHOLD_PERCENT) {
            System.out.println("   ⚠️  [Health] Queue depth at " + depthPercent
                               + "% — scale up consumers!");
        }
    }

    public int depth()    { return queue.size(); }
    public int capacity() { return capacity; }
}

package queue;

import model.Message;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * <h1>Dead-Letter Queue (DLQ)</h1>
 *
 * <p>In production, when a message fails to process after N retries it must
 * <em>not</em> be silently dropped — that leads to invisible data loss.
 * Instead it is routed here for manual inspection, alerting, or replay.
 *
 * <p>This queue is intentionally unbounded in size (or very large) because
 * DLQ entries are written rarely and consumed by an operator, not a
 * high-throughput worker.
 *
 * <p><strong>Real-world equivalent:</strong> SQS Dead-Letter Queue,
 * Kafka DLQ topic, RabbitMQ dead-letter exchange.
 */
public class DeadLetterQueue {

    private static final int MAX_DLQ_SIZE = 1000;
    private final BlockingQueue<Message> dlq = new ArrayBlockingQueue<>(MAX_DLQ_SIZE);

    /** Called by a Consumer when it gives up after max retries. */
    public void route(Message message) {
        boolean added = dlq.offer(message);
        if (added) {
            System.out.println(
                "   ☠️  [DLQ] Message routed to Dead-Letter Queue: " + message
                    + " | DLQ depth: " + dlq.size()
            );
        } else {
            System.err.println("   ❌ [DLQ] CRITICAL: DLQ is FULL! Message lost: " + message);
        }
    }

    public int depth()              { return dlq.size(); }
    public boolean isEmpty()        { return dlq.isEmpty(); }

    /** Operator/admin drains DLQ messages for inspection. */
    public Message take() throws InterruptedException { return dlq.take(); }

    public void printReport() {
        System.out.println("\n📋 [DLQ Report] " + dlq.size() + " messages failed processing:");
        dlq.forEach(m -> System.out.println("     - " + m));
    }
}

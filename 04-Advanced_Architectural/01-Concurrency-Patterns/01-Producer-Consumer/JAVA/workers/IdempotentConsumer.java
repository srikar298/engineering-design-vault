package workers;

import model.Message;
import queue.MessageQueue;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <h1>Idempotent Consumer</h1>
 *
 * <p>The most critical fix over the v1 Consumer. This class solves the
 * <strong>"At-Least-Once Delivery" duplicate problem</strong>:
 *
 * <pre>
 * Timeline of a duplicate:
 *   1. Consumer receives Message(id=abc, payload="send email")
 *   2. Consumer sends the email ✅
 *   3. Network drops before Consumer can ACK the queue
 *   4. Queue re-delivers Message(id=abc) to another Consumer
 *   5. ❌ User receives the same email TWICE
 * </pre>
 *
 * <p>Solution: before processing, check if {@code correlationId} was already
 * handled. If yes, skip it. The Set acts as an in-memory idempotency key
 * store (in production this would be a Redis {@code SETNX} call).
 *
 * <h2>Dead-Letter Queue (DLQ) integration</h2>
 * <p>If processing genuinely fails (not a duplicate), the message is retried
 * up to {@link #MAX_RETRIES} times. After that it is routed to the DLQ
 * instead of being silently dropped.
 */
public class IdempotentConsumer implements Runnable {

    private static final int MAX_RETRIES = 3;

    /** Shared across all Consumer instances — simulates a Redis cache. */
    private final Set<String> processedIds;

    private final MessageQueue queue;
    private final int id;

    public IdempotentConsumer(int id, MessageQueue queue,
                              Set<String> processedIds) {
        this.id           = id;
        this.queue        = queue;
        this.processedIds = processedIds;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Message message = queue.consume();
                handleWithIdempotency(message);
            }
        } catch (InterruptedException e) {
            System.out.println("🛑 [Consumer-" + id + "] Interrupted, shutting down.");
            Thread.currentThread().interrupt();
        }
    }

    private void handleWithIdempotency(Message message) {
        // ---------------------------------------------------------------
        // Idempotency Check
        // ConcurrentHashMap.add() is atomic — safe under multi-threading.
        // In production: Redis SETNX with a TTL would replace this Set.
        // ---------------------------------------------------------------
        if (!processedIds.add(message.getCorrelationId())) {
            System.out.println("   ♻️  [Consumer-" + id + "] DUPLICATE detected, skipping: "
                               + message.getCorrelationId().substring(0, 8) + "...");
            return;
        }

        // Try processing with retries
        boolean success = false;
        Message current = message;

        while (current.getRetryCount() < MAX_RETRIES) {
            try {
                process(current);
                success = true;
                break;
            } catch (Exception e) {
                System.out.println("   ⚠️  [Consumer-" + id + "] Attempt "
                                   + (current.getRetryCount() + 1) + " failed: " + e.getMessage());
                current = current.withIncrementedRetry();
                try { Thread.sleep(200L * (current.getRetryCount())); } // simple backoff
                catch (InterruptedException ie) { Thread.currentThread().interrupt(); return; }
            }
        }

        if (!success) {
            // Route to DLQ instead of losing the message silently
            queue.sendToDLQ(current);
        }
    }

    /**
     * Simulates actual business logic — e.g. sending an email, writing to DB.
     * Throws an exception to demonstrate the retry + DLQ flow when needed.
     */
    private void process(Message message) {
        System.out.println("   ✅ [Consumer-" + id + "] Processed: " + message);
        // Uncomment the next line to simulate a processing failure:
        // if (message.getPayload().contains("FAIL")) throw new RuntimeException("Processing error");
    }
}

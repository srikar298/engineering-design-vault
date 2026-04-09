package model;

import java.util.UUID;

/**
 * <h1>The Message Object</h1>
 *
 * <p>Every message carries a unique {@code correlationId} which enables
 * <strong>Idempotent Consumption</strong>: if a Consumer crashes after
 * processing but before acknowledging, the broker re-delivers the message.
 * The Consumer uses the correlationId to detect and skip the duplicate.
 *
 * <p>This is non-negotiable in financial/event-driven systems.
 */
public final class Message {

    private final String correlationId;
    private final String payload;
    private int retryCount;

    public Message(String payload) {
        this.correlationId = UUID.randomUUID().toString();
        this.payload = payload;
        this.retryCount = 0;
    }

    /** Used when reconstructing a message for retry */
    public Message(String correlationId, String payload, int retryCount) {
        this.correlationId = correlationId;
        this.payload = payload;
        this.retryCount = retryCount;
    }

    public String getCorrelationId() { return correlationId; }
    public String getPayload()       { return payload; }
    public int    getRetryCount()    { return retryCount; }

    public Message withIncrementedRetry() {
        return new Message(correlationId, payload, retryCount + 1);
    }

    @Override
    public String toString() {
        return "Message{id=" + correlationId.substring(0, 8) + "..., payload='" + payload
               + "', retry=" + retryCount + "}";
    }
}

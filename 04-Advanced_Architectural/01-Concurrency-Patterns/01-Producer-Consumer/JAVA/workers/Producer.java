package workers;

import model.Message;
import queue.MessageQueue;

/**
 * <h1>Updated Producer</h1>
 *
 * <p>Now produces typed {@link Message} objects (instead of raw Strings)
 * so every message carries its own {@code correlationId} for idempotent
 * consumption downstream.
 */
public class Producer implements Runnable {

    private final MessageQueue queue;
    private final int id;
    private final int messageCount;

    public Producer(int id, MessageQueue queue, int messageCount) {
        this.id           = id;
        this.queue        = queue;
        this.messageCount = messageCount;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= messageCount; i++) {
                Message msg = new Message("Event-" + i + " from Producer-" + id);
                queue.produce(msg);
                Thread.sleep(100); // simulate event generation rate
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

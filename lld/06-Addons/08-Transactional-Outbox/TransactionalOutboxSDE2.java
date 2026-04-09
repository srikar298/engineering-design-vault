package addons.distributed;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>08 - Transactional Outbox (The "Atomic" Event Broadcaster)</h1>
 * 
 * <b>Scenario:</b> At 10k users, your system is likely distributed. If you 
 * update the Database but the Message Broker (Kafka/RabbitMQ) fails, your 
 * system becomes inconsistent (e.g., User is charged, but Order is never created).
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>At-Least-Once Delivery:</b> The message is only marked as 'SENT' after 
 *    the broker acknowledges it. This prevents message loss.
 * 2. <b>Atomic Transaction:</b> The main data update and the outbox entry 
 *    happen in the SAME DB transaction.
 * 3. <b>Idempotency:</b> Consumers must handle the same message multiple times 
 *    (in case the sender retries).
 */

class OutboxMessage {
    String id;
    String payload;
    boolean isSent;
    OutboxMessage(String id, String payload) { this.id = id; this.payload = payload; this.isSent = false; }
}

class OrderService {
    // Simulated DB Tables
    private final List<String> orders = new ArrayList<>();
    private final List<OutboxMessage> outboxTable = new ArrayList<>();

    public void createOrder(String orderId) {
        // --- [INTERVIEW_MVP] (Atomic Transaction) ---
        System.out.println("[DB] Starting Transaction...");
        
        // 1. Update Business Data
        orders.add(orderId);
        System.out.println("[DB] Saved Order: " + orderId);

        // 2. Update Outbox (In the same transaction)
        outboxTable.add(new OutboxMessage(orderId, "Order Created: " + orderId));
        System.out.println("[DB] Saved Outbox Entry for: " + orderId);
        
        System.out.println("[DB] Transaction Committed ✅");
    }

    public void processOutbox() {
        System.out.println("\n--- Outbox Processor Starting ---");
        for (OutboxMessage msg : outboxTable) {
            if (!msg.isSent) {
                // Simulate Broker Call (Kafka/SNS)
                boolean brokerAck = simulateBrokerPublish(msg.payload);
                if (brokerAck) {
                    msg.isSent = true;
                    System.out.println("[BROKER] Ack Received. Message " + msg.id + " marked as SENT.");
                }
            }
        }
    }

    private boolean simulateBrokerPublish(String msg) {
        System.out.println("[BROKER] Publishing: " + msg);
        return true; // Simulate success
    }
}

public class TransactionalOutboxSDE2 {
    public static void main(String[] args) {
        OrderService service = new OrderService();
        
        // 1. Create Order (Atomically updates DB and Outbox)
        service.createOrder("ORDER_123");
        
        // 2. Background worker publishes the event
        service.processOutbox();
    }
}

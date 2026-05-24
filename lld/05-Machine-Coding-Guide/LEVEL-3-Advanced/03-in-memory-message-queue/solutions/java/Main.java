import mq.*;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== In-Memory Message Queue LLD Simulation ===");
        MessageBroker broker = MessageBroker.getInstance();

        // 1. Create a topic with 3 partitions
        String topicName = "orders-topic";
        broker.createTopic(topicName, 3);

        // 2. Register consumer group
        ConsumerGroup group = broker.registerConsumerGroup("order-processing-group", topicName);

        // 3. Create consumer instances
        ConsumerInstance c1 = new ConsumerInstance("C1", group);
        ConsumerInstance c2 = new ConsumerInstance("C2", group);

        ExecutorService executor = Executors.newCachedThreadPool();
        
        // Register and start C1 and C2
        group.registerConsumer(c1);
        group.registerConsumer(c2);
        executor.submit(c1);
        executor.submit(c2);

        // 4. Publish some messages
        System.out.println("\n--- Publishing messages (Initial) ---");
        for (int i = 1; i <= 6; i++) {
            String key = (i % 2 == 0) ? "user-" + (i / 2) : null;
            broker.publish(topicName, new Message(
                    UUID.randomUUID().toString(),
                    "Order-Data-" + i,
                    key
            ));
        }

        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        // 5. Add a 3rd consumer (C3) - triggers rebalance
        System.out.println("\n--- Registering Consumer C3 ---");
        ConsumerInstance c3 = new ConsumerInstance("C3", group);
        group.registerConsumer(c3);
        executor.submit(c3);

        System.out.println("\n--- Publishing messages (After C3 Join) ---");
        for (int i = 7; i <= 12; i++) {
            broker.publish(topicName, new Message(
                    UUID.randomUUID().toString(),
                    "Order-Data-" + i,
                    null
            ));
        }

        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        // 6. Deregister C1 - triggers rebalance
        System.out.println("\n--- Deregistering Consumer C1 ---");
        c1.stop();
        group.deregisterConsumer(c1);

        System.out.println("\n--- Publishing final messages ---");
        for (int i = 13; i <= 15; i++) {
            broker.publish(topicName, new Message(
                    UUID.randomUUID().toString(),
                    "Order-Data-" + i,
                    "user-special"
            ));
        }

        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        // 7. Clean shutdown
        System.out.println("\n--- Shutting down simulation ---");
        c2.stop();
        c3.stop();
        executor.shutdownNow();
        System.out.println("Simulation finished cleanly.");
    }
}

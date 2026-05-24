package mq;

import java.util.List;

public class ConsumerInstance implements Runnable {
    private final String id;
    private final ConsumerGroup group;
    private volatile boolean running = true;

    public ConsumerInstance(String id, ConsumerGroup group) {
        this.id = id;
        this.group = group;
    }

    public String getId() { return id; }

    public void stop() {
        this.running = false;
    }

    @Override
    public void run() {
        System.out.println("Consumer " + id + " started running.");
        while (running) {
            try {
                List<Integer> assignedPartitions = group.getAssignedPartitions(this);
                if (assignedPartitions.isEmpty()) {
                    Thread.sleep(100); // Wait for partition assignment
                    continue;
                }

                boolean consumedAny = false;
                for (int partitionId : assignedPartitions) {
                    Partition partition = group.getTopic().getPartitions().get(partitionId);
                    int currentOffset = group.getOffset(partitionId);
                    
                    List<Message> batch = partition.read(currentOffset, 5); // Read max 5 messages
                    if (!batch.isEmpty()) {
                        consumedAny = true;
                        for (Message msg : batch) {
                            System.out.printf("[%s - Consumer %s] Consumed partition %d offset %d: %s\n",
                                    group.getGroupId(), id, partitionId, currentOffset, msg.getPayload());
                            currentOffset++;
                        }
                        group.commitOffset(partitionId, currentOffset);
                    }
                }

                if (!consumedAny) {
                    Thread.sleep(100); // Backoff if no messages
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Error in consumer instance " + id + ": " + e.getMessage());
            }
        }
        System.out.println("Consumer " + id + " stopped.");
    }
}

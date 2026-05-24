package mq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ConsumerGroup {
    private final String groupId;
    private final Topic topic;
    private final List<ConsumerInstance> consumers = new ArrayList<>();
    private final Map<Integer, Integer> partitionOffsets = new ConcurrentHashMap<>();
    private final Map<Integer, ConsumerInstance> partitionAssignment = new ConcurrentHashMap<>();
    private final ReentrantLock rebalanceLock = new ReentrantLock();

    public ConsumerGroup(String groupId, Topic topic) {
        this.groupId = groupId;
        this.topic = topic;
        // Initialize offsets to 0 for all partitions
        for (Partition p : topic.getPartitions()) {
            partitionOffsets.put(p.getId(), 0);
        }
    }

    public String getGroupId() { return groupId; }
    public Topic getTopic() { return topic; }

    public void registerConsumer(ConsumerInstance consumer) {
        rebalanceLock.lock();
        try {
            consumers.add(consumer);
            System.out.printf("[%s] Registered consumer %s. Triggering rebalance.\n", groupId, consumer.getId());
            rebalance();
        } finally {
            rebalanceLock.unlock();
        }
    }

    public void deregisterConsumer(ConsumerInstance consumer) {
        rebalanceLock.lock();
        try {
            consumers.remove(consumer);
            System.out.printf("[%s] Deregistered consumer %s. Triggering rebalance.\n", groupId, consumer.getId());
            rebalance();
        } finally {
            rebalanceLock.unlock();
        }
    }

    public void rebalance() {
        rebalanceLock.lock();
        try {
            partitionAssignment.clear();
            if (consumers.isEmpty()) {
                System.out.printf("[%s] No active consumers. All partitions unassigned.\n", groupId);
                return;
            }

            List<Partition> partitions = topic.getPartitions();
            int numConsumers = consumers.size();

            for (int i = 0; i < partitions.size(); i++) {
                Partition partition = partitions.get(i);
                ConsumerInstance assignedConsumer = consumers.get(i % numConsumers);
                partitionAssignment.put(partition.getId(), assignedConsumer);
                System.out.printf("[%s] Assigned partition %d to consumer %s\n", 
                        groupId, partition.getId(), assignedConsumer.getId());
            }
        } finally {
            rebalanceLock.unlock();
        }
    }

    public List<Integer> getAssignedPartitions(ConsumerInstance consumer) {
        List<Integer> assigned = new ArrayList<>();
        for (Map.Entry<Integer, ConsumerInstance> entry : partitionAssignment.entrySet()) {
            if (entry.getValue().getId().equals(consumer.getId())) {
                assigned.add(entry.getKey());
            }
        }
        return assigned;
    }

    public int getOffset(int partitionId) {
        return partitionOffsets.getOrDefault(partitionId, 0);
    }

    public void commitOffset(int partitionId, int offset) {
        partitionOffsets.put(partitionId, offset);
    }
}

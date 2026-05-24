package mq;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Topic {
    private final String name;
    private final List<Partition> partitions;
    private final AtomicInteger roundRobinCounter = new AtomicInteger(0);

    public Topic(String name, int partitionCount) {
        this.name = name;
        this.partitions = new ArrayList<>(partitionCount);
        for (int i = 0; i < partitionCount; i++) {
            this.partitions.add(new Partition(i));
        }
    }

    public String getName() { return name; }
    public List<Partition> getPartitions() { return partitions; }

    public int publish(Message message) {
        Partition targetPartition = selectPartition(message);
        return targetPartition.append(message);
    }

    private Partition selectPartition(Message message) {
        int index;
        if (message.getKey() != null) {
            index = Math.abs(message.getKey().hashCode()) % partitions.size();
        } else {
            index = Math.abs(roundRobinCounter.getAndIncrement()) % partitions.size();
        }
        return partitions.get(index);
    }
}

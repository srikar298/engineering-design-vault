package mq;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageBroker {
    private static MessageBroker instance;
    private final Map<String, Topic> topics = new ConcurrentHashMap<>();
    private final Map<String, ConsumerGroup> consumerGroups = new ConcurrentHashMap<>();

    private MessageBroker() {}

    public static synchronized MessageBroker getInstance() {
        if (instance == null) {
            instance = new MessageBroker();
        }
        return instance;
    }

    public Topic createTopic(String name, int partitionCount) {
        Topic topic = new Topic(name, partitionCount);
        topics.put(name, topic);
        System.out.printf("Created topic '%s' with %d partitions.\n", name, partitionCount);
        return topic;
    }

    public Topic getTopic(String name) {
        return topics.get(name);
    }

    public void publish(String topicName, Message message) {
        Topic topic = topics.get(topicName);
        if (topic == null) {
            throw new IllegalArgumentException("Topic " + topicName + " does not exist.");
        }
        int offset = topic.publish(message);
        // Find which partition it landed in for logging purposes
        // By checking key/hash:
        int partitionId;
        if (message.getKey() != null) {
            partitionId = Math.abs(message.getKey().hashCode()) % topic.getPartitions().size();
        } else {
            // Re-evaluating counter is not exact for logging sincecounter was incremented, 
            // but we can just inspect which partition appended it by checking message ID inside partition.
            partitionId = -1;
            for (Partition p : topic.getPartitions()) {
                // Since this is in-memory, we can check size, but let's just log offset
                // To be simple, we don't need exact partition logs here, we log message append.
            }
        }
        System.out.printf("Published: [Topic: %s] Payload: '%s' Offset: %d\n", topicName, message.getPayload(), offset);
    }

    public ConsumerGroup registerConsumerGroup(String groupId, String topicName) {
        Topic topic = topics.get(topicName);
        if (topic == null) {
            throw new IllegalArgumentException("Topic " + topicName + " does not exist.");
        }
        ConsumerGroup group = new ConsumerGroup(groupId, topic);
        consumerGroups.put(groupId, group);
        return group;
    }
}

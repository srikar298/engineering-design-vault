package mq;

public class Message {
    private final String id;
    private final String payload;
    private final String key;
    private final long timestamp;

    public Message(String id, String payload, String key) {
        this.id = id;
        this.payload = payload;
        this.key = key;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getPayload() { return payload; }
    public String getKey() { return key; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("Message{id='%s', key='%s', payload='%s'}", id, key, payload);
    }
}

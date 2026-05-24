package chatsystem;

public class Message {
    private final String messageId;
    private final String senderId;
    private final String recipientId;
    private final String content;
    private final long timestamp;
    private final MessageType type;
    private MessageStatus status;

    public Message(String messageId, String senderId, String recipientId, String content, MessageType type) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.type = type;
        this.status = MessageStatus.SENT;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public MessageType getType() {
        return type;
    }

    public synchronized MessageStatus getStatus() {
        return status;
    }

    public synchronized void setStatus(MessageStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("[MsgId: %s] %s -> %s: \"%s\" [%s]", messageId, senderId, recipientId, content, status);
    }
}

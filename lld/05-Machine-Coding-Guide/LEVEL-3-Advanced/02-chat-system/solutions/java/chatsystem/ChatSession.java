package chatsystem;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatSession {
    private final String userId;
    private final Queue<Message> receivedMessages;

    public ChatSession(String userId) {
        this.userId = userId;
        this.receivedMessages = new ConcurrentLinkedQueue<>();
    }

    public String getUserId() {
        return userId;
    }

    public void deliver(Message message) {
        message.setStatus(MessageStatus.DELIVERED);
        receivedMessages.add(message);
        System.out.println(String.format("   [Session-%s] Push received: %s", userId, message));
    }

    public Queue<Message> getReceivedMessages() {
        return receivedMessages;
    }
}

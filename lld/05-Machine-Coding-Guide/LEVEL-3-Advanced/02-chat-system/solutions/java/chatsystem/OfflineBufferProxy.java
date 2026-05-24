package chatsystem;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class OfflineBufferProxy {
    private final String userId;
    private final Queue<Message> bufferedMessages;
    private final ReentrantLock lock;

    public OfflineBufferProxy(String userId) {
        this.userId = userId;
        this.bufferedMessages = new ConcurrentLinkedQueue<>();
        this.lock = new ReentrantLock();
    }

    public void deliverOrBuffer(Message message, SessionRegistry sessionRegistry) {
        lock.lock();
        try {
            ChatSession session = sessionRegistry.getSession(userId);
            if (session != null) {
                session.deliver(message);
            } else {
                bufferedMessages.add(message);
                System.out.println(String.format("   [OfflineBuffer-%s] Buffered offline message: %s", userId, message.getMessageId()));
            }
        } finally {
            lock.unlock();
        }
    }

    public void flushToSession(ChatSession session) {
        lock.lock();
        try {
            if (!bufferedMessages.isEmpty()) {
                System.out.println(String.format("   [OfflineBuffer-%s] Reconnect: flushing %d buffered messages.", userId, bufferedMessages.size()));
            }
            while (!bufferedMessages.isEmpty()) {
                Message msg = bufferedMessages.poll();
                session.deliver(msg);
            }
        } finally {
            lock.unlock();
        }
    }

    public int getBufferSize() {
        return bufferedMessages.size();
    }
}

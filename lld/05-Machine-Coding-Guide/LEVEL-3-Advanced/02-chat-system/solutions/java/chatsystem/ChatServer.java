package chatsystem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ChatServer {
    private static ChatServer instance;
    private static final ReentrantLock instanceLock = new ReentrantLock();

    private final SessionRegistry sessionRegistry;
    private final Map<String, OfflineBufferProxy> offlineBuffers;
    private final Map<String, ChatRoom> activeRooms;

    private ChatServer() {
        this.sessionRegistry = new SessionRegistry();
        this.offlineBuffers = new ConcurrentHashMap<>();
        this.activeRooms = new ConcurrentHashMap<>();
    }

    public static ChatServer getInstance() {
        if (instance == null) {
            instanceLock.lock();
            try {
                if (instance == null) {
                    instance = new ChatServer();
                }
            } finally {
                instanceLock.unlock();
            }
        }
        return instance;
    }

    public void registerRoom(ChatRoom room) {
        activeRooms.put(room.getRoomId(), room);
    }

    public void connectUser(User user, ChatSession session) {
        System.out.println(String.format("[Server] User %s (%s) connected.", user.getName(), user.getUserId()));
        sessionRegistry.register(user.getUserId(), session);
        
        // Retrieve offline buffer proxy and flush
        OfflineBufferProxy bufferProxy = getOrCreateOfflineBuffer(user.getUserId());
        bufferProxy.flushToSession(session);
    }

    public void disconnectUser(String userId) {
        System.out.println(String.format("[Server] User %s disconnected.", userId));
        sessionRegistry.unregister(userId);
    }

    public void sendMessage(Message message) {
        if (message.getType() == MessageType.GROUP) {
            ChatRoom room = activeRooms.get(message.getRecipientId());
            if (room != null) {
                room.sendMessage(message, this);
            } else {
                System.out.println(String.format("[Server] Error: Group room %s not found.", message.getRecipientId()));
            }
        } else {
            deliverToUser(message.getRecipientId(), message);
        }
    }

    public void deliverToUser(String recipientId, Message message) {
        OfflineBufferProxy bufferProxy = getOrCreateOfflineBuffer(recipientId);
        bufferProxy.deliverOrBuffer(message, sessionRegistry);
    }

    public void markAsRead(String userId, String messageId, Message message) {
        if (message.getMessageId().equals(messageId) && message.getRecipientId().equals(userId)) {
            message.setStatus(MessageStatus.READ);
            System.out.println(String.format("[Server] Message %s read by %s.", messageId, userId));
        }
    }

    private OfflineBufferProxy getOrCreateOfflineBuffer(String userId) {
        return offlineBuffers.computeIfAbsent(userId, id -> new OfflineBufferProxy(id));
    }
}

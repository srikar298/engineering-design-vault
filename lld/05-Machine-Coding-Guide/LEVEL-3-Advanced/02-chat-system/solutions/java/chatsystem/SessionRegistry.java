package chatsystem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionRegistry {
    private final Map<String, ChatSession> activeSessions;

    public SessionRegistry() {
        this.activeSessions = new ConcurrentHashMap<>();
    }

    public void register(String userId, ChatSession session) {
        activeSessions.put(userId, session);
    }

    public void unregister(String userId) {
        activeSessions.remove(userId);
    }

    public ChatSession getSession(String userId) {
        return activeSessions.get(userId);
    }

    public boolean isOnline(String userId) {
        return activeSessions.containsKey(userId);
    }
}

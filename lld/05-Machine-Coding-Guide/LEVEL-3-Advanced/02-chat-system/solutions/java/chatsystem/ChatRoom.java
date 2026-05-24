package chatsystem;

public abstract class ChatRoom {
    protected final String roomId;
    protected final String roomName;

    protected ChatRoom(String roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public abstract void sendMessage(Message message, ChatServer server);
}

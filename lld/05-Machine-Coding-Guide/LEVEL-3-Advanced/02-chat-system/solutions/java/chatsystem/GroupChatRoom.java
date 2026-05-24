package chatsystem;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GroupChatRoom extends ChatRoom {
    private final List<String> memberIds;

    public GroupChatRoom(String roomId, String roomName) {
        super(roomId, roomName);
        this.memberIds = new CopyOnWriteArrayList<>();
    }

    public void addMember(String userId) {
        if (!memberIds.contains(userId)) {
            memberIds.add(userId);
        }
    }

    public void removeMember(String userId) {
        memberIds.remove(userId);
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    @Override
    public void sendMessage(Message message, ChatServer server) {
        System.out.println(String.format("[Room-%s] Broadcasting message %s from %s to members.", roomName, message.getMessageId(), message.getSenderId()));
        for (String memberId : memberIds) {
            // Do not deliver back to the sender
            if (!memberId.equals(message.getSenderId())) {
                server.deliverToUser(memberId, message);
            }
        }
    }
}

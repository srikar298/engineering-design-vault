import chatsystem.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("💬 Enterprise Chat System Simulation & Demo 💬");
        System.out.println("=================================================\n");

        ChatServer server = ChatServer.getInstance();

        // 1. Register Users
        User alice = new User("USR-001", "Alice");
        User bob = new User("USR-002", "Bob");
        User charlie = new User("USR-003", "Charlie");

        // 2. Connect Users (Simulating WebSocket Session Initialization)
        System.out.println("--- 🏁 Initializing Connections ---");
        ChatSession aliceSession = new ChatSession(alice.getUserId());
        server.connectUser(alice, aliceSession);

        ChatSession bobSession = new ChatSession(bob.getUserId());
        server.connectUser(bob, bobSession);

        System.out.println("\n--- ✉️ Scenario 1: Individual Direct Message ---");
        Message msg1 = new Message("MSG-1", alice.getUserId(), bob.getUserId(), "Hi Bob, are we on for the LLD review?", MessageType.INDIVIDUAL);
        System.out.println("Alice sends message MSG-1 to Bob...");
        server.sendMessage(msg1);
        
        // Assert delivery status
        System.out.println("Message status: " + msg1.getStatus()); // Expected: DELIVERED
        
        // Bob reads the message
        System.out.println("Bob reading message MSG-1...");
        server.markAsRead(bob.getUserId(), "MSG-1", msg1);
        System.out.println("Message status: " + msg1.getStatus()); // Expected: READ


        System.out.println("\n--- 👥 Scenario 2: Group Chat Messaging ---");
        // Create Group Room
        GroupChatRoom groupRoom = new GroupChatRoom("GRP-101", "LLD Study Group");
        groupRoom.addMember(alice.getUserId());
        groupRoom.addMember(bob.getUserId());
        groupRoom.addMember(charlie.getUserId()); // Charlie is offline
        server.registerRoom(groupRoom);

        System.out.println("Group 'LLD Study Group' created with Alice, Bob, and Charlie (offline).");
        Message groupMsg = new Message("MSG-GRP-1", alice.getUserId(), "GRP-101", "Hey group! Check out the pattern mapping.", MessageType.GROUP);
        System.out.println("Alice sends message to LLD Study Group...");
        server.sendMessage(groupMsg);


        System.out.println("\n--- ⏸️ Scenario 3: Offline Buffering & Reconnection Flow ---");
        System.out.println("Bob disconnects...");
        server.disconnectUser(bob.getUserId());

        Message msg2 = new Message("MSG-2", alice.getUserId(), bob.getUserId(), "Bob, did you see the group message?", MessageType.INDIVIDUAL);
        System.out.println("Alice sends message MSG-2 to Bob (while Bob is offline)...");
        server.sendMessage(msg2);

        System.out.println("\nBob reconnects...");
        ChatSession bobNewSession = new ChatSession(bob.getUserId());
        server.connectUser(bob, bobNewSession);
        
        // Verify message msg2 got delivered upon reconnection
        System.out.println("Bob's new session message count: " + bobNewSession.getReceivedMessages().size());
        System.out.println("MSG-2 Status: " + msg2.getStatus()); // Expected: DELIVERED


        System.out.println("\n--- ⚡ Scenario 4: Concurrency and Race Condition Testing ---");
        System.out.println("Charlie is offline. We will fire 10 concurrent messages to Charlie from Alice using 10 threads.");
        System.out.println("This tests thread-safe queue updates and lock isolation on Charlie's offline buffer proxy.");

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCounter = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    Message concurrentMsg = new Message("CONC-MSG-" + id, alice.getUserId(), charlie.getUserId(), "Concurrent test message " + id, MessageType.INDIVIDUAL);
                    server.sendMessage(concurrentMsg);
                    successCounter.incrementAndGet();
                } catch (Exception e) {
                    System.err.println("Thread execution failed: " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Executor interrupted: " + e.getMessage());
        }

        System.out.println("Concurrent messages dispatched successfully: " + successCounter.get());

        System.out.println("\nCharlie connects to retrieve messages...");
        ChatSession charlieSession = new ChatSession(charlie.getUserId());
        server.connectUser(charlie, charlieSession);

        // Verify Charlie received all group and concurrent individual messages
        // Expecting: 1 Group Message + 10 Concurrent Messages = 11 Messages
        int totalExpected = 11;
        int totalActual = charlieSession.getReceivedMessages().size();
        System.out.println("\n--- Concurrency Test Summary ---");
        System.out.println("Total Messages Received by Charlie: " + totalActual + " (Expected: " + totalExpected + ")");
        if (totalExpected == totalActual) {
            System.out.println("🟢 SUCCESS: Concurrency controls and offline buffers worked perfectly!");
        } else {
            System.out.println("🔴 FAILURE: Messages were lost or duplicated.");
        }
    }
}

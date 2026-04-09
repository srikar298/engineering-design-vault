package addons.distributed;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h1>15 - Lamport Logical Clocks (DDIA Mastery)</h1>
 * 
 * <b>Scenario:</b> In a distributed system with 10k users, events happen on 
 * different servers. Server A's clock might be 5ms ahead of Server B's. 
 * Using <code>System.currentTimeMillis()</code> will result in incorrect event 
 * ordering. 
 * 
 * <b>The DDIA Solution:</b> Use a Logical Clock (Lamport Timestamp) to 
 * establish a "Happens-Before" relationship without relying on physical time.
 * 
 * <b>Rules of Lamport Clocks:</b>
 * 1. Each process increments its counter before every local event.
 * 2. When sending a message, include the current counter.
 * 3. When receiving a message, set counter to <code>max(local, remote) + 1</code>.
 */

class DistributedNode {
    private final String nodeId;
    private final AtomicInteger logicalClock = new AtomicInteger(0);

    public DistributedNode(String id) { this.nodeId = id; }

    public int localEvent() {
        int time = logicalClock.incrementAndGet();
        System.out.println("[Node " + nodeId + "] Local Event at T=" + time);
        return time;
    }

    public int sendEvent() {
        int time = logicalClock.incrementAndGet();
        System.out.println("[Node " + nodeId + "] Sending Message at T=" + time);
        return time;
    }

    public void receiveEvent(int remoteTime, String senderId) {
        // Core Logic: max(local, remote) + 1
        int currentTime;
        int nextTime;
        do {
            currentTime = logicalClock.get();
            nextTime = Math.max(currentTime, remoteTime) + 1;
        } while (!logicalClock.compareAndSet(currentTime, nextTime));
        
        System.out.println("[Node " + nodeId + "] Received from " + senderId + 
                           ". Clock adjusted from " + currentTime + " to " + nextTime);
    }
}

public class LamportClockSDE2 {
    public static void main(String[] args) {
        DistributedNode serverA = new DistributedNode("A");
        DistributedNode serverB = new DistributedNode("B");

        // 1. Server A does local work
        serverA.localEvent();

        // 2. Server A sends a message to B
        int sentTime = serverA.sendEvent();

        // 3. Server B receives it (even if B's physical clock was slow)
        serverB.receiveEvent(sentTime, "A");

        // 4. Server B does work
        serverB.localEvent();
    }
}

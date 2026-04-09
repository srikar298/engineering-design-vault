package addons.distributed;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>17 - Distributed Leader Election (Bully Algorithm - DDIA Chapter 9)</h1>
 * 
 * <b>Scenario:</b> In a cluster of 5 nodes, Node 5 is the "Leader" (Master). 
 * If Node 5 crashes, the remaining nodes must elect a new Leader to handle writes.
 * 
 * <b>The Bully Algorithm:</b>
 * 1. A node notices the leader is down. It sends an "Election" message to all 
 *    nodes with a HIGHER ID.
 * 2. If no one higher responds, it becomes the Leader and informs everyone.
 * 3. If a higher node responds, it takes over the process.
 * 
 * <b>SDE-3 Insights:</b>
 * 1. <b>Failure Detection:</b> Usually done via heartbeats (e.g., node doesn't 
 *    ping for 5 seconds).
 * 2. <b>Consensus:</b> Real systems use Raft or Paxos for more robust 
 *    guarantees, but Bully is the classic interview algorithm.
 */

class Node {
    public final int id;
    public boolean isActive = true;

    public Node(int id) { this.id = id; }

    public void elect(List<Node> allNodes) {
        System.out.println("[Node " + id + "] Starting Election...");
        boolean foundHigherNode = false;

        for (Node other : allNodes) {
            if (other.id > this.id && other.isActive) {
                System.out.println("   -> Notifying Node " + other.id);
                foundHigherNode = true;
                other.elect(allNodes); // Pass the torch to higher node
                break; 
            }
        }

        if (!foundHigherNode) {
            System.out.println("[RESULT] Node " + id + " is the new LEADER! 👑");
            announceLeader(id, allNodes);
        }
    }

    private void announceLeader(int leaderId, List<Node> allNodes) {
        for (Node node : allNodes) {
            if (node.id != leaderId) {
                System.out.println("   -> Node " + node.id + " acknowledges Leader " + leaderId);
            }
        }
    }
}

public class LeaderElectionSDE2 {
    public static void main(String[] args) {
        List<Node> cluster = new ArrayList<>();
        for (int i = 1; i <= 5; i++) cluster.add(new Node(i));

        // 1. Simulate Leader (Node 5) crashing
        System.out.println("--- LEADER CRASH: Node 5 is Down ---");
        cluster.get(4).isActive = false;

        // 2. Node 2 notices and starts an election
        System.out.println("--- Node 2 initiates Election ---");
        cluster.get(1).elect(cluster);
        
        System.out.println("\nSenior Signal: 'To maintain high availability in our " +
                           "distributed clusters, we implement automated Leader Election " +
                           "using Raft-inspired heartbeats, ensuring a recovery time (MTTR) under 2 seconds.'");
    }
}

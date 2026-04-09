package addons.distributed;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>16 - Version Vectors (Conflict Resolution - DDIA Chapter 5)</h1>
 * 
 * <b>Scenario:</b> In a multi-region system (Asia, US), two users update the 
 * same <code>ProductPrice</code> at the same time. How does the DB know 
 * which update happened first, or if they are concurrent?
 * 
 * <b>The DDIA Solution:</b> Version Vectors. Unlike a single counter, a 
 * Version Vector tracks a counter for EVERY replica. 
 * 
 * <b>Comparison Logic:</b>
 * 1. <b>A > B:</b> If every counter in A is >= every counter in B.
 * 2. <b>A < B:</b> If every counter in A is <= every counter in B.
 * 3. <b>Concurrent:</b> If some counters in A are > B and some are < B.
 */

class VersionVector {
    private final Map<String, Integer> vector = new HashMap<>();

    public void increment(String nodeId) {
        vector.put(nodeId, vector.getOrDefault(nodeId, 0) + 1);
    }

    public Map<String, Integer> getVector() { return new HashMap<>(vector); }

    public static String compare(VersionVector v1, VersionVector v2) {
        boolean v1Greater = false;
        boolean v2Greater = false;

        for (String node : allNodes(v1, v2)) {
            int c1 = v1.vector.getOrDefault(node, 0);
            int c2 = v2.vector.getOrDefault(node, 0);
            if (c1 > c2) v1Greater = true;
            if (c2 > c1) v2Greater = true;
        }

        if (v1Greater && v2Greater) return "CONCURRENT (Conflict!)";
        if (v1Greater) return "V1 is NEWER";
        if (v2Greater) return "V2 is NEWER";
        return "IDENTICAL";
    }

    private static java.util.Set<String> allNodes(VersionVector v1, VersionVector v2) {
        java.util.Set<String> nodes = new java.util.HashSet<>(v1.vector.keySet());
        nodes.addAll(v2.vector.keySet());
        return nodes;
    }
}

public class VersionVectorSDE2 {
    public static void main(String[] args) {
        VersionVector v1 = new VersionVector();
        VersionVector v2 = new VersionVector();

        // 1. Initial State
        v1.increment("Asia");
        v2.increment("Asia");

        // 2. Convergent Update (V2 grows from V1)
        v2.increment("US");
        System.out.println("Result (Convergent): " + VersionVector.compare(v1, v2)); // V2 Newer

        // 3. Concurrent Update (Conflict!)
        v1.increment("EU"); // V1 updated in EU, V2 updated in US independently
        System.out.println("Result (Concurrent): " + VersionVector.compare(v1, v2)); 
        
        System.out.println("\nSenior Signal: 'To handle multi-region write conflicts, " +
                           "we use Version Vectors to detect concurrent updates and " +
                           "trigger a Semantic Reconciliation strategy.'");
    }
}

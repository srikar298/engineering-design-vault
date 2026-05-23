# ⚡ 02 - Write Conflict Resolution

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C066 |
| **Category** | Multi-Region |
| **Difficulty** | 🔴 Hard |
| **Interview Frequency** | 🔥 High |
| **Understanding** | 🔴 None / 🟡 Conceptual / 🟢 Applied |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | 2026-06-01 |
| **Mastery** | 🔴 Familiar / 🟡 Competent / 🟢 Expert |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Write Conflict Resolution encompasses the algorithms and structural patterns used to reconcile conflicting updates made concurrently to the same data record across different regional master databases. System architects trigger these strategies when designing Active-Active (multi-primary) architectures where geographic latency makes real-time distributed locking impossible, necessitating that writes are accepted locally and synchronized asynchronously.
*   **Scalability Dimension:** Primary: **Data Consistency Model**. Secondary: **Database Write Throughput** and **Application-level CPU/Memory Overhead**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### The Conflict Scenario
Consider two users editing a user profile simultaneously in different regions:
1.  User A in New York edits `profile_img` to `ny.png`. The request hits `US-East-1`.
2.  User B in London edits `profile_img` to `london.png`. The request hits `EU-West-1`.
3.  Both regions accept the write locally and queue an asynchronous replication message to the other.
4.  When the replication messages arrive, both regions detect that the same database row was modified concurrently. How do we resolve this without locking?

---

### Conflict Resolution Strategies

#### 1. Last-Write-Wins (LWW)
*   **Mechanism:** Each write is stamped with the physical wall-clock timestamp of the database node that accepted it. When a conflict occurs, the write with the higher timestamp is kept; the other is discarded.
*   **The Clock Drift Problem:** Real physical clocks drift. Using **NTP (Network Time Protocol)**, servers can easily drift by 10-50ms. If the clock on the database node in `EU-West-1` is drifted forward by 100ms, it will overwrite writes in `US-East-1` that occurred later in real time. This leads to **silent data loss** and causality violations.

```
Node US-East (Time: 10:00:00.010) ──── Write: "A" ────────┐
                                                         ▼
                                                [Conflict Engine] ──► Winner: "B" (Drifted)
                                                         ▲
Node EU-West (Time: 10:00:00.120) ──── Write: "B" ────────┘
(Clock drifted forward by 110ms)
```

#### 2. Vector Clocks / Version Vectors
*   **Mechanism:** A logical clock representation consisting of a map of `(NodeID -> SequenceNumber)`. Every time a node updates a record, it increments its own sequence number in that record's vector clock.
*   **Conflict Detection:** 
    *   Vector $V_1$ dominates (happened-before) $V_2$ if for all nodes $k$, $V_1[k] \ge V_2[k]$ and at least one element is strictly greater.
    *   If neither dominates (e.g., $V_1 = [A:1, B:0]$ and $V_2 = [A:0, B:1]$), the updates are **concurrent**. The database retains both values (creating siblings) and pushes the responsibility of merging the data to the client application (as seen in Amazon DynamoDB).

```
Vector Clock Tracking:
Initial state: [A:0, B:0]
Node A updates: [A:1, B:0] (Data: "X") ────► Replicates to B ──► Node B State: [A:1, B:0]
Node A updates: [A:2, B:0] (Data: "Y") ┐
                                       ├─── Concurrent! Conflict detected.
Node B updates: [A:1, B:1] (Data: "Z") ┘
```

#### 3. Conflict-Free Replicated Data Types (CRDTs)
*   **Mechanism:** Mathematical object types designed to merge automatically when replicated across nodes without coordination.
*   **State-based CRDTs (CvRDT):** Nodes send their full local state to other nodes. The merge operator $\sqcup$ must be:
    *   *Associative:* $(x \sqcup y) \sqcup z = x \sqcup (y \sqcup z)$
    *   *Commutative:* $x \sqcup y = y \sqcup x$
    *   *Idempotent:* $x \sqcup x = x$
*   *Examples:* **G-Counter** (Grow-only counter), **PN-Counter** (Positive-Negative counter), **LWW-Element-Set** (Last-Write-Wins element set).

---

### Comparison of Resolution Strategies

| Strategy | Data Loss Risk | Clock Sync Dependency | Operational Complexity | Use Cases | Implementation Cost |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Last-Write-Wins (LWW)** | 🔴 High (discarded concurrent writes). | 🔴 Critical (requires absolute sync). | 🟢 Low (built-in in Cassandra/DynamoDB). | Analytics logs, telemetry (where minor loss is fine). | 🟢 Minimal. |
| **Vector Clocks** | 🟢 None (all concurrent versions kept). | 🟢 None (uses logical sequence). | 🔴 High (requires application-level merge). | E-commerce shopping carts, session preferences. | 🔴 High (requires client-side logic). |
| **CRDTs** | 🟢 None (mathematically unified). | 🟢 None (commutes order-independent). | 🟡 Medium (requires using specialized types). | Collaborative text editing (Figma, Notion), social network likes. | 🟡 Medium (requires CRDT library/db). |
| **Application Custom Rules** | 🟡 Medium (depends on business logic). | 🟢 None. | 🔴 High (custom rule logic per endpoint). | Bank ledgers (always append transaction logs, never overwrite). | 🔴 High. |

---

## 💥 3. Resiliency & Operations

*   **Observability (The "Signal"):**
    *   `Server Clock Drift (Milliseconds)`: Monitor via system telemetry (e.g., `chronyc sources -v`). If any node drift exceeds 10ms, alert immediately to prevent corrupt LWW writes.
    *   `Database Sibling Count / Version Conflict Rate`: A rising percentage of writes triggering vector clock conflicts indicates high concurrent access to the same records, warning of potential performance issues.
*   **Blast Radius (The "Impact"):**
    *   If vector clocks grow unchecked (e.g., a record updated millions of times by different transient servers), the metadata overhead (vector key-value mapping) can dwarf the actual payload.
    *   **Mitigation:** Configure **Vector Clock Pruning**. Automatically drop the oldest node-sequence entries from the vector clock once the map size crosses a threshold (e.g., 50 entries).

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Advocating for Active-Active multi-master databases without mentioning conflict resolution entirely, assuming the database "magically handles it."
*   Proposing LWW for a bank ledger application. If a client deposits \$100 in Region A and \$50 in Region B simultaneously, LWW will result in one deposit overwriting the other, losing customer funds. The correct answer is to model updates as an immutable append-only ledger log rather than in-place overwrites.

### Interview Tip (The "Strong Hire" Signal)
> *"We implemented an Active-Active Cassandra cluster across US and EU. Because NTP clock drift is a physical reality, we avoided Cassandra's default LWW for our shopping cart service. Instead, we modeled the shopping cart as a state-based ORDT (Observed-Remove Set) CRDT. This mathematically guaranteed that whether an add or remove operation arrived out-of-order, the final cart state converged identically in both regions without data loss."*

---

## 💡 5. My Custom Study Notes & Whiteboard

### G-Counter (Grow-Only Counter CRDT) Java Implementation
This simple G-Counter demonstrates the commutative, associative, and idempotent properties.

```java
import java.util.HashMap;
import java.util.Map;

public class GCounter {
    private final String nodeId;
    private final Map<String, Integer> state;

    public GCounter(String nodeId) {
        this.nodeId = nodeId;
        this.state = new HashMap<>();
        this.state.put(nodeId, 0);
    }

    // Local increment
    public synchronized void increment() {
        state.put(nodeId, state.get(nodeId) + 1);
    }

    // Read counter value by summing all node sequences
    public synchronized int getValue() {
        return state.values().stream().mapToInt(Integer::intValue).sum();
    }

    // Merge incoming state from another node (Lub / Join operator)
    public synchronized void merge(GCounter incoming) {
        for (Map.Entry<String, Integer> entry : incoming.state.entrySet()) {
            String peerNode = entry.getKey();
            int peerVal = entry.getValue();
            
            // Core Merge Rule: x = max(local[k], incoming[k])
            int localVal = this.state.getOrDefault(peerNode, 0);
            this.state.put(peerNode, Math.max(localVal, peerVal));
        }
    }

    @Override
    public String toString() {
        return "GCounterState=" + state + ", Value=" + getValue();
    }
}
```

### Vector Clock Concurrency Checker Algorithm
Given two vector clocks $A$ and $B$:
```
1. If A == B: Return "Equal"
2. Let GreaterA = false, GreaterB = false
3. For each key in union(A.keys, B.keys):
    valA = A.getOrDefault(key, 0)
    valB = B.getOrDefault(key, 0)
    if valA > valB: GreaterA = true
    if valB > valA: GreaterB = true
4. If GreaterA and GreaterB: Return "Concurrent/Conflict"
5. If GreaterA: Return "A dominates B (A is descendant)"
6. If GreaterB: Return "B dominates A (B is descendant)"
```

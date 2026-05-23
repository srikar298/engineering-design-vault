# 🕰️ Distributed Clocks: Ordering Events (C070, C071)

In a distributed system, you cannot rely on "Wall Clock Time" (System Time) because clocks on different machines drift. If Machine A thinks it's 10:00:01 and Machine B thinks it's 10:00:02, you cannot use these timestamps to determine which event happened first.

---

## 1. Lamport Timestamps (Logical Clocks) (C071)

**Concept:** A simple counter incremented on every event. 
- Every message sent includes the sender's current counter.
- When a node receives a message, it sets its own counter to `max(local_counter, received_counter) + 1`.

**Limitation:** It tells you if Event A *might* have caused Event B, but it cannot tell you if two events were concurrent.

---

## 2. Vector Clocks (C070)

**Concept:** Instead of a single number, each node maintains an **array (vector)** of counters—one for every node in the cluster.
- **Example:** `[NodeA: 1, NodeB: 3, NodeC: 0]`
- When Node A sends to Node B, it sends its entire vector. Node B merges it.

**Trade-off:**
- **Pros:** Can detect **Concurrent Writes** (Conflicts). If two vectors are incomparable (e.g., `[1, 0]` vs `[0, 1]`), they happened simultaneously.
- **Cons:** Size of the vector grows with the number of nodes ($O(N)$). Not suitable for clusters with thousands of nodes.

---

## 3. Hybrid Logical Clocks (HLC)

Used in databases like CockroachDB. It combines the precision of physical NTP clocks with the causal ordering of Lamport clocks.

---

## 🧠 Tracker Integration (C070, C071)

*   **Core Trade-off:** Accuracy vs Complexity.
*   **The "Senior Signal":** Mentioning **Clock Drift** as the reason why `System.currentTimeMillis()` is dangerous for database sequence IDs.
*   **Interview Trap:** Assuming all nodes in a cluster have the exact same time.

### 🔬 Self-Assessment Prompts
1. Why can't we just use NTP (Network Time Protocol) for strict event ordering?
2. How does a Vector Clock represent a "Conflict"?
3. What is the "Last Write Wins" (LWW) strategy, and how does it relate to clocks?

# ⚡ 01 - Distributed Locking

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C086 |
| **Category** | Distributed Coordination |
| **Difficulty** | 🟡 Medium |
| **Interview Frequency** | 🔥 High |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Distributed Locking is a coordination mechanism that ensures mutually exclusive access to shared resources across multiple independent servers in a cluster. It prevents concurrent threads running on different machines from modifying the same resource simultaneously (e.g., allocating a booking seat), using external consensus nodes (like Redis or ZooKeeper).
*   **Scalability Dimension:** Primary: **Data Consistency** & **Concurrency Control**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Implementation Strategies
1.  **Redis-Based Lock (Redlock / SETNX):**
    *   *Mechanism:* Uses Redis `SET key value NX PX 10000` to set a key only if it does not exist, with an expiration lease (TTL) to prevent deadlocks on server crashes.
    *   *Pros:* Extremely fast (sub-millisecond locks).
    *   *Cons:* Can fail if clocks drift or if a master crashes before replicating the lock to replicas.
2.  **Consensus-Based Lock (ZooKeeper / etcd):**
    *   *Mechanism:* Uses ephemeral nodes and sequential watches. If a client crashes, its session times out and ZooKeeper deletes the lock node.
    *   *Pros:* Strongly consistent (CP system). Guarded against cluster splits.
    *   *Cons:* Higher latency due to Raft/Paxos network round-trips.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Lock acquisition wait time`: High wait times indicate lock contention.
    *   `Lock lease expirations (lock timeouts)`: Occurs when client operations take longer than the lock lease TTL.
*   **Blast Radius (The "Impact"):**
    *   If a client takes longer than the lock TTL to complete its work, the lock is released while the client is still processing. Another client acquires the lock, leading to concurrent updates and data corruption.
*   *Solution:* Use **Lock Renewal (Redisson Watchdog)** to extend lease times dynamically, or **Fencing Tokens**.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Forgetting to set a TTL on the lock key (if the lock holder server crashes, the lock remains active forever, creating a permanent deadlock).
*   Assuming Redis single-master locks are 100% reliable for high-value financial transactions (clocks or failover steps can cause double lock leases).

### Interview Tip (The "Strong Hire" Signal)
> *"To ensure mutual exclusion across our cluster, we use Redis for distributed locks with a lease TTL. If the task exceeds the TTL, we use a background renewal thread (Watchdog) to refresh the lease, and validate writes at the database layer using **Fencing Tokens**."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

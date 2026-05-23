# ⚡ 08 - Linearizability

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C080 |
| **Category** | Consistency Models |
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
*   **Two-Sentence Trigger:** Linearizability (also called Strong Consistency or External Consistency) is the strongest single-object, single-operation consistency model, guaranteeing that every operation appears to take effect instantaneously at some point in time between its start and its completion, relative to a global real-time physical clock. It is triggered when designing critical coordination locks (like ZooKeeper/Etcd lock acquisitions) or unique-name registrations (like domain name buying) where a client must never read stale state once a write completes.
*   **Scalability Dimension:** Primary: **Speed-of-Light Network Latency & Global Clock Drift vs. Real-Time correctness**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Linearizability in Action
A write operation takes a physical time window to complete. Once the write succeeds (at the commit point), all subsequent reads must see this write:
```
Physical Time ───────────────────────────────────────────────────────────────►
  Client A:  |─── Write(X=1) ───|  (Commit Point)
  Client B:        |── Read(X) ──|  <-- Can return X=0 or X=1 (overlaps write)
  Client C:                           |── Read(X) ──| <-- MUST return X=1 (after write)
```

### Linearizability vs. Serializability vs. Sequential Consistency
*   **Linearizability (Single-Object, Physical Time):**
    *   Concerned with individual read/write operations on single objects.
    *   Requires operations to match physical physical-world time.
*   **Serializability (Multi-Object, Logical Transactions):**
    *   Concerned with transactions containing multiple reads/writes across multiple rows.
    *   Guarantees that execution results are equivalent to some serial order, but does *not* require that order to match physical time.
*   **Sequential Consistency (Single-Object, Logical Time):**
    *   Weaker than linearizability. Requires that all replicas agree on the exact same order of operations, but does *not* require that order to match real physical time. (e.g., if Write A happens at 1:00 PM and Write B at 1:05 PM, sequential consistency allows the cluster to process B then A, as long as *all* nodes process B then A).

---

### Enforcing Linearizability at Scale
How do modern databases implement linearizability without global lock deadlocks?
1. **Raft/Paxos Single Leader:**
   * Reads and writes must go through the active leader.
   * *The Leak:* If the leader is partitioned from the cluster but doesn't know it yet, it might serve stale reads (**Split-Brain**).
   * *Mitigation:* The leader must verify its lease with a majority of nodes (via heartbeats) before responding to any read request.
2. **Google TrueTime (Spanner):**
   * Uses synchronized GPS receivers and Atomic Clocks in every datacenter.
   * Clock drift is bound to an uncertainty window $[t-\epsilon, t+\epsilon]$, where $\epsilon$ is typically $< 7\text{ms}$.
   * **Commit Wait:** To write, the database assigns a commit timestamp $t$ and blocks the write from returning until $2\epsilon$ time has passed, ensuring no future transaction can get a timestamp before $t$.

---

## 💥 3. Resiliency & Operations

### Operational Pitfalls & Mitigations
*   **TrueTime Drift Escalation:**
    *   *Problem:* If both GPS receivers and atomic clocks fail in a Spanner datacenter, the clock drift uncertainty ($\epsilon$) expands. Commit waits increase from 7ms to seconds, causing write throughput to collapse.
    *   *Mitigation:* Use dual-path time sync (GPS + local NTP) and route database traffic away from zones reporting high clock drift.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Confusing Linearizability with Serializability. SDE-2+ candidates must know: Linearizability is about single-operation physical time; Serializability is about multi-operation transactional grouping.
*   Suggesting simple database replication with asynchronous secondaries is linearizable. Secondaries are always stale, breaking the real-time guarantee.

### Interview Tip (The "Strong Hire" Signal)
> *"We enforce Linearizability for our distributed lock manager using Etcd. To prevent split-brain reads where a partitioned leader serves stale locks, Etcd forces the leader to contact a quorum of followers to verify its lease before responding to any read request. This guarantees that once a lock is acquired or released, the change is instantly visible to all clients globally, relative to real physical time."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

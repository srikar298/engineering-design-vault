# ⚡ 09 - Paxos Algorithm

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C083 |
| **Category** | Distributed Consensus |
| **Difficulty** | 🔥 Hard |
| **Interview Frequency** | 🟡 Medium |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Paxos is a foundational distributed consensus algorithm that allows a cluster of nodes to agree on a single value even if some nodes fail or messages are delayed. It operates in two phases — Prepare and Accept — guaranteeing safety (no two nodes commit different values) but sacrificing liveness (the cluster can stall if proposals continuously conflict).
*   **Scalability Dimension:** Primary: **Strong Consistency (Safety)** & **Fault Tolerance**. Secondary: Negative impact on **Latency (2 network round-trips per proposal)** & **Throughput (sequential consensus bottleneck)**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### The Two-Phase Execution

**Phase 1 — Prepare:**
1. A **Proposer** selects a proposal number $n$ (higher than any it has used before) and broadcasts a `PREPARE(n)` message to all **Acceptors**.
2. Each Acceptor, if $n$ is higher than any prepare it has responded to, replies with a `PROMISE(n, v)` — promising to reject any future proposals with a number less than $n$, and returning the highest-numbered value $v$ it has already accepted (if any).

**Phase 2 — Accept:**
1. If the Proposer receives a Quorum ($\lceil N/2 \rceil + 1$) of promises, it sends `ACCEPT(n, v)` where $v$ is either the value from the highest-numbered promise received or a new value if no acceptors had accepted anything yet.
2. Acceptors that have not promised a higher-numbered proposal accept the value and notify **Learners**.
3. Once a quorum of acceptors accepts, consensus is reached — the value is committed.

### Paxos vs. Raft
| Attribute | Paxos (Multi-Paxos) | Raft |
| :--- | :--- | :--- |
| **Understandability** | Notoriously complex. Requires years of expertise to implement correctly. | Explicitly designed for understandability. Easy to implement. |
| **Leader** | Optional (leaderless Paxos variants exist). | Mandatory: One leader at all times. |
| **Log Replication** | Not specified by base Paxos; needs Multi-Paxos extension. | Built-in sequential log replication. |
| **Used By** | Google Chubby, Google Spanner. | etcd, CockroachDB, TiKV. |

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Proposal Conflict Rate`: High conflict rates indicate multiple concurrent Proposers (a liveness issue — the system stalls but never corrupts data).
    *   `Consensus Round-Trip Latency`.
*   **Blast Radius (The "Impact"):**
    *   Paxos guarantees *safety* absolutely — two different values can never be committed. However, if two Proposers continuously preempt each other with higher proposal numbers, the cluster can livelock and make zero progress indefinitely.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Confusing Paxos with 2PC (Two-Phase Commit). Paxos is a consensus protocol that can survive node failures. 2PC is a transaction protocol that blocks if the coordinator fails.
*   Saying "Paxos guarantees liveness" — it does not. Paxos only guarantees *safety*. Raft adds explicit leader leases to improve liveness.

### Interview Tip (The "Strong Hire" Signal)
> *"In practice, I would never implement raw Paxos — it's notoriously difficult to implement correctly. I'd use **Raft** via etcd or CockroachDB's built-in consensus layer. If asked about Paxos, I understand its theoretical role as the foundation for Multi-Paxos used in Google Chubby and Spanner."*

---

## 💡 5. My Custom Study Notes & Whiteboard
```
Paxos Two-Phase Summary:

Phase 1 (Prepare):   Proposer ──PREPARE(n)──► Majority of Acceptors
                     Acceptors ──PROMISE(n, last_v)──► Proposer

Phase 2 (Accept):    Proposer ──ACCEPT(n, v)──► Majority of Acceptors
                     Acceptors ──ACCEPTED──► Learners

Consensus achieved when a QUORUM of Acceptors send ACCEPTED.
```

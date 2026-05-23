# ⚡ 11 - Quorum

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C085 |
| **Category** | Distributed Consistency |
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
*   **Two-Sentence Trigger:** A Quorum is the minimum number of nodes in a distributed cluster that must agree on a read or write operation before it is considered valid. By enforcing $W + R > N$ (where $W$ = write quorum, $R$ = read quorum, $N$ = total replicas), any read set and any write set are guaranteed to overlap on at least one node, ensuring the most recent write is always visible on every read.
*   **Scalability Dimension:** Primary: **Data Consistency (Tunable)** & **Fault Tolerance**. Secondary: Trade-off between **Write Latency** (higher $W$) and **Read Latency** (higher $R$).

---

## ⚖️ 2. Trade-offs & Deep Dive

### The Fundamental Quorum Math
With $N$ total replica nodes, the quorum rule is:

$$W + R > N$$

This guarantees that the read set and the write set always share at least one node — ensuring at least one node on any read has the latest data.

### Common Quorum Configurations
| Configuration | $N$ | $W$ | $R$ | Consistency | Trade-off |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Strong Consistency** | 3 | 2 | 2 | ✅ Strong | Both reads and writes wait for 2 nodes. Higher latency. |
| **Write-Optimized** | 3 | 1 | 3 | ✅ Strong | Writes are fast (ack from 1). Reads must check all 3 nodes. |
| **Read-Optimized** | 3 | 3 | 1 | ✅ Strong | Reads are fast (ack from 1). All 3 nodes must confirm writes. |
| **Eventual Consistency** | 3 | 1 | 1 | ❌ Eventual | Maximum availability. No consistency guarantee. |

### The Sloppy Quorum (Dynamo-Style)
In systems like DynamoDB and Cassandra, during a network partition, the write quorum may be satisfied by nodes *outside* the standard replica set ("hinted handoff"). This means:
*   Writes succeed even if designated replicas are down — maximum availability.
*   But the system is no longer truly quorum-consistent until the hinted handoff replays after the partition heals.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Write/Read Quorum Failure Rate`: Tracks how often insufficient replicas are available to satisfy the quorum.
    *   `Hinted Handoff Queue Depth` (Cassandra): Growing queues indicate a replica node is down and pending data resync.
*   **Blast Radius (The "Impact"):**
    *   If the number of **live replicas** drops below the required write quorum $W$, writes are rejected. Clients experience write errors until enough replicas recover.
    *   If live replicas drop below read quorum $R$, reads are rejected too — complete service outage.
*   **Numbers to Know:**
    *   Most production systems default to **$N=3$, $W=2$, $R=2$** (the balanced quorum — tolerates 1 node failure for both reads and writes).
    *   Cassandra defaults: `QUORUM` consistency level (majority of all replicas).

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Stating "Quorum means majority" without knowing the formula — quorum is *configurable*. $W=1, R=1$ is also a valid quorum (with no consistency).
*   Not knowing that $W + R > N$ is the consistency guarantee condition. Interviewers love asking "prove why this works."
*   Forgetting that sloppy quorums (Dynamo) break the strict $W + R > N$ guarantee during partitions.

### Interview Tip (The "Strong Hire" Signal)
> *"For our user profile reads, we configure Cassandra with $N=3, W=2, R=2$. This satisfies $W + R = 4 > N = 3$, guaranteeing that any read overlaps with the latest write. We accept the trade-off of both reads and writes needing 2 out of 3 nodes alive — tolerating a single replica failure."*

---

## 💡 5. My Custom Study Notes & Whiteboard
```
Quorum Overlap Proof (N=3, W=2, R=2):

Write touches: Nodes {A, B}         (2 nodes)
Read touches:  Nodes {B, C}         (2 nodes)
Overlap:       Node {B}             ← ALWAYS at least 1 overlap

B has the latest write → read always returns fresh data ✅

Counterexample (W=1, R=1 → Eventual Consistency):
Write touches: Node {A}
Read touches:  Node {C}
Overlap:       {} (NONE) → C may return stale data ❌
```

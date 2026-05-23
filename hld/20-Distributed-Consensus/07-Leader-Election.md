# ⚡ 07 - Leader Election

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C082 |
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
*   **Two-Sentence Trigger:** Leader Election is the process by which a cluster of distributed nodes autonomously and consistently designates one node as the "leader" responsible for coordinating writes, scheduling jobs, or managing cluster state. It is essential for databases (e.g., ensuring a single write master), distributed schedulers (e.g., Kubernetes controller-manager), and any system that requires a single authoritative coordinator.
*   **Scalability Dimension:** Primary: **High Availability (Failover Speed)** & **Data Consistency (Single Leader Authority)**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Common Leader Election Algorithms
| Algorithm | Mechanism | Pros | Cons |
| :--- | :--- | :--- | :--- |
| **Bully Algorithm** | The node with the highest ID wins. During election, a node sends an "Election" message to all nodes with higher IDs. If no response, it declares itself leader. | Simple to implement. | Does not scale — $O(N^2)$ messages. Susceptible to frequent re-elections if the highest-ID node is flaky. |
| **Ring Election** | Nodes are arranged in a logical ring. A node initiates an election message that travels around the ring; each node appends its ID. The highest ID becomes leader. | $O(N)$ message complexity. | Requires synchronized ring topology; a dead node breaks the ring. |
| **Consensus-Based (Raft/ZooKeeper)** | Uses a quorum-based voting round. A candidate must receive a majority of votes. Used by etcd, ZooKeeper, CockroachDB. | Guaranteed safety (no split-brain). Widely battle-tested. | Higher latency (network round-trips for quorum). |

### The Split-Brain Problem
If a network partition occurs, two nodes might both believe they are the leader, causing **dual-master writes** and data corruption.
*   *Solution (Fencing / STONITH):* When a new leader is elected, it revokes the previous leader's write tokens (Fencing Tokens). All storage engines reject writes from tokens below the current epoch/term number.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Leader Term Changes (Raft Term Counter)`: Frequent term changes indicate cluster instability or network flaps.
    *   `Election Duration (ms)`: Time taken from leader failure detection to new leader becoming active. Directly equals client write downtime.
*   **Blast Radius (The "Impact"):**
    *   During the election window (typically 1–3 seconds in Raft), **all writes to the cluster are blocked**. Clients receive timeouts until a new leader is elected and begins accepting writes.
*   **Numbers to Know:**
    *   Typical Raft election timeout: **150–300ms randomized** (to prevent split votes).
    *   etcd leader election completion: **~500ms – 2s** in production.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Not addressing the split-brain scenario when describing leader election (any complete answer must include how the system prevents two concurrent leaders).
*   Proposing a fixed election timeout (e.g., 1s for all nodes) without explaining that randomized timeouts prevent split votes where all candidates start simultaneously.

### Interview Tip (The "Strong Hire" Signal)
> *"For our distributed scheduler, we use ZooKeeper's ephemeral nodes for leader election. Each candidate creates an ephemeral sequential znode. The node with the lowest sequence number is the leader. If it dies, ZooKeeper deletes its ephemeral node and the next lowest znode holder becomes leader automatically — no split-brain possible."*

---

## 💡 5. My Custom Study Notes & Whiteboard
```
ZooKeeper Ephemeral Node Leader Election:

Candidate A → Creates: /election/leader-0001 (LOWEST → LEADER)
Candidate B → Creates: /election/leader-0002 (Watches 0001)
Candidate C → Creates: /election/leader-0003 (Watches 0002)

If A crashes → ZK deletes /leader-0001 → B gets notified → B becomes leader
If B crashes → ZK deletes /leader-0002 → C gets notified → C becomes leader
```

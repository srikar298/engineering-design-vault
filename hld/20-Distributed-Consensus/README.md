# 🤝 20 - Distributed Consensus (Raft & Paxos)

## 📖 1. The Concept
In a distributed system with 10k nodes, how do they all agree on a single value (e.g., "Who is the Master?") despite network failures? This is **Consensus**.

---

## 🏛️ 2. The Raft Algorithm (SDE-2 Standard)
Raft is designed to be understandable. It works through three states: **Follower**, **Candidate**, and **Leader**.

### The Flow:
1.  **Leader Election**: If followers don't hear from a leader (Heartbeat timeout), they become candidates and start an election.
2.  **Log Replication**: The leader accepts client requests, appends them to its log, and tells followers to do the same.
3.  **Commit**: Once a **Quorum** (majority) of followers acknowledge, the entry is "committed" and safe.

---

## ⚖️ 3. Raft vs. Paxos

| Feature | Paxos | Raft |
| :--- | :--- | :--- |
| **Complexity** | Extremely High (Math-heavy). | Moderate (Logic-heavy). |
| **Usage** | Google Spanner, Zookeeper (ZAB variant). | etcd (Kubernetes), CockroachDB, TiDB. |
| **Understandability** | Difficult. | Designed for classrooms and code. |

---

## 🚀 4. The SDE-3 Edge: Linearizability
When nodes agree on a value, they provide **Linearizability** (Strong Consistency). 
*Senior Signal:* "We use etcd for service discovery because its Raft-based consensus provides linearizable reads/writes, ensuring that all our 10k nodes see the exact same service configuration at the same time."

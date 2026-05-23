# ⚡ 01 - Database Replication

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C060 |
| **Category** | Database Scaling |
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
*   **Two-Sentence Trigger:** Database Replication is the process of copying data across multiple server nodes to ensure high availability, fault tolerance, and read scalability. It allows the system to continue operating even if some database nodes crash, using topologies like single-leader, multi-leader, or leaderless setups.
*   **Scalability Dimension:** Primary: **Read QPS** & **High Availability (Fault Tolerance)**. Secondary: Negative impact on **Write Consistency**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| Topology | Pros | Cons | The SDE-2 Challenge |
| :--- | :--- | :--- | :--- |
| **Single-Leader** | Simplest model. No write conflicts. | Single point of failure for writes. Scaling writes is limited. | **Replication Lag:** Users might read stale data from slow replicas. |
| **Multi-Leader** | Survives WAN/datacenter outages. Scale writes across regions. | Extremely high write conflict resolution complexity (LWW, CRDTs). | **Conflict Resolution:** Resolving concurrent modifications in different nodes. |
| **Leaderless (Dynamo)** | High write/read scalability, maximum availability. | Read repair overhead. Complex consensus math. | **Quorums:** Enforcing $W + R > N$ configurations. |

*   **Ideal Use Cases:**
    *   Standard SaaS web applications requiring high read capacity and master failover redundancy.
*   **Anti-Patterns / When NOT to use:**
    *   Using multi-leader replication for systems with low write latency tolerance and no conflict-resolution infrastructure.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Replication Lag Duration (Seconds)`: Crucial for master-slave setups. High values indicate replicas are failing to keep up with master writes.
    *   `Replica Network IO and Sync backlogs`.
*   **Blast Radius (The "Impact"):**
    *   If replication lag grows too large, clients reading from replicas experience stale data, breaking business flows. If the master fails during heavy replication lag, data loss can occur.

---

## 🚫 4. Interview Playbook
*   **Common Mistakes:**
    *   Assuming replication solves write capacity bottlenecks (all replicas must replicate all writes; replication does not scale write capacity).
    *   Not knowing how replication lag affects application states.
*   **Interview Tip (The "Strong Hire" Signal):**
    *   Demonstrate consistency mitigation: *"I will configure our database replication using Single-Leader. To mitigate replication lag issues, we will implement **Read-Your-Own-Writes consistency**—directing users to read from the master node for 60 seconds after performing any write action."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

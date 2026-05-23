# ⚡ 01 - Consistent Hashing

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C045 |
| **Category** | Load Balancing |
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
*   **Two-Sentence Trigger:** Consistent Hashing is a data partitioning algorithm that maps both cache/database nodes and query keys onto a circular hash ring (0 to $2^{32}-1$). Keys are routed clockwise to the first node they encounter, ensuring that adding or removing a node only requires redistributing $1/N$ of the keys, preventing system-wide cache storms.
*   **Scalability Dimension:** Primary: **Read/Write Scalability** & **Cache Hit Ratio Preservation**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| Modulo Hashing (`hash(k) % N`) | Consistent Hashing with Virtual Nodes |
| :--- | :--- |
| **Modulo Hashing:** Directly maps key to array offset using count $N$. | **Consistent Hashing:** Maps nodes and keys to points on a 360-degree ring. |
| *Pros:* Zero execution overhead. Simple implementation. | *Pros:* Adding/removing nodes changes only $1/N$ of the keys. |
| *Cons:* If $N$ changes, almost all keys map to new nodes (100% cache miss storm). | *Cons:* More complex lookup math (requires binary search tree traversal). |

### The Virtual Nodes Strategy
If you map physical servers directly to points on the ring, you get **Hot Spots** (non-uniform distribution).
*   *Solution:* **Virtual Nodes (VNodes)**. Each physical server is assigned multiple tokens on the ring (e.g., Server A maps to A1, A2, A3... up to 100+ points). This ensures that even if a node fails, its workload is split uniformly among all remaining nodes, rather than dumping all its traffic onto a single clockwise neighbor.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Key distribution variance across nodes`: High standard deviation indicates uneven slot distribution (increase VNode count).
    *   `Cache Miss Rate` spike during re-sharding.
*   **Blast Radius (The "Impact"):**
    *   If a node dies, the immediate clockwise neighbor inherits its keys. If VNodes are not configured, this neighbor can be overwhelmed, trigger a cascading crash, and take down the entire cluster (cascading failure).

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Forgetting virtual nodes when whiteboarding consistent hashing (this leads to poor load distribution).
*   Not explaining *how* VNodes are mapped back to physical IPs (requires an in-memory routing table/sorted map).

### Interview Tip (The "Strong Hire" Signal)
> *"To scale our distributed state tier dynamically, we use Consistent Hashing with 256 virtual nodes per physical instance. This guarantees that when nodes auto-scale out, we only redistribute $1/N$ of our cached sessions, preventing a database thundering herd."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

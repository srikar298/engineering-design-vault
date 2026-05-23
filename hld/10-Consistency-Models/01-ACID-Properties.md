# ⚡ 01 - ACID Properties

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C067 |
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
*   **Two-Sentence Trigger:** ACID (Atomicity, Consistency, Isolation, Durability) is a foundational set of database properties that guarantee transactional safety and data integrity despite hardware failures, crashes, or high concurrent access. It is triggered when implementing multi-step data modifications (such as financial transfers or inventory checkouts) that must execute as a single atomic unit, preventing partial writes and race conditions.
*   **Scalability Dimension:** Primary: **Strong Database Consistency vs. Concurrency Latency**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Transaction Isolation Levels vs. Anomalies
To provide the "I" in ACID, databases use isolation levels, trading off throughput for correctness:
| Isolation Level | Dirty Reads | Non-Repeatable Reads | Phantom Reads | Write Skew | Implementation Cost |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Read Uncommitted**| ⚠️ Allowed | ⚠️ Allowed | ⚠️ Allowed | ⚠️ Allowed | 🟢 Lowest (No locks for reads). |
| **Read Committed** | ✅ Prevented | ⚠️ Allowed | ⚠️ Allowed | ⚠️ Allowed | 🟡 Low (Short read locks). |
| **Repeatable Read** | ✅ Prevented | ✅ Prevented | ⚠️ Allowed | ⚠️ Allowed | 🔴 Medium (Shared/Exclusive locks).|
| **Serializable** | ✅ Prevented | ✅ Prevented | ✅ Prevented | ✅ Prevented | 💀 Highest (Two-Phase Locking / SSI).|

---

### ACID Breakdown Details
1. **Atomicity ("All or Nothing"):**
   * *Mechanism:* The database writes modifications to a **Write-Ahead Log (WAL)** on disk before modifying actual data pages. If a transaction crashes halfway, the DB reads the WAL to execute a rollback, returning data to its pre-transaction state.
2. **Consistency (Invariants):**
   * *Mechanism:* Ensures the database transitions from one valid state to another, maintaining constraints, foreign keys, and unique indexes. If a transaction attempts to insert a record that violates a constraint, the database aborts the transaction.
   * *Note:* This is distinct from consistency in the CAP theorem (which is about read correctness across replicas).
3. **Isolation (Concurrency Control):**
   * *Mechanism:* Restricts how concurrent transactions see each other's changes.
   * *Implementation:* Relational engines use either **Two-Phase Locking (2PL)** or **Multi-Version Concurrency Control (MVCC)** (which creates versioned snapshots of records, allowing reads to bypass write locks).
4. **Durability (Non-volatile Storage):**
   * *Mechanism:* Ensures that once a transaction commits, its state is guaranteed to persist even during power outages. This requires calling `fsync()` to force the OS file system buffer to flush data from volatile RAM to physical disk.

---

### The Cost of ACID in Distributed Systems
*   **The Write Latency Penalty:** In a single-node database, ACID is managed via local memory and disk writes. In a distributed database, enforcing ACID requires **Two-Phase Commit (2PC)** or consensus protocols (Paxos/Raft) across network boundaries.
*   **Coordinating Locks:** Locking rows across 10 servers to guarantee isolation stops writes globally on those tables, resulting in severe throughput collapse and deadlock risks.

---

## 💥 3. Resiliency & Operations

### Operational Pitfalls & Mitigations
*   **Deadlock Saturation:**
    *   *Problem:* Transaction 1 locks Row A and waits for Row B. Transaction 2 locks Row B and waits for Row A. Both wait indefinitely, consuming database connection pool threads.
    *   *Mitigation:* Keep transactions short, lock rows in a consistent alphanumeric order across all queries, and configure low transaction lock timeouts (e.g., 5 seconds) to abort stalled transactions.
*   **Disk Bottleneck due to Fsync:**
    *   *Problem:* Calling `fsync()` on every single commit slows database write rates to the IOPS limits of the disk (e.g., 100-200 writes/sec on traditional HDDs).
    *   *Mitigation:* Use Group Commits (batching multiple transaction fsyncs together) or host the database on SSDs with battery-backed write cache.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Confusing database consistency in ACID with consistency in the CAP theorem. (ACID Consistency = schema constraints; CAP Consistency = reading the latest write from any replica node).
*   Recommending "Serializable" isolation for every relational database table. This shuts down concurrent database execution and ruins application scale.

### Interview Tip (The "Strong Hire" Signal)
> *"For our transaction ledger, we require ACID properties to prevent double-spending. To scale this Relational Database tier, we configure Snapshot Isolation via MVCC, which allows reads to proceed without locking writes. We isolate write transaction operations to minimal updates and order our queries alphanumeric-by-primary-key to completely prevent distributed deadlocks."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

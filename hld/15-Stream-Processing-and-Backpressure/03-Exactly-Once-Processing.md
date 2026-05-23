# ⚡ 03 - Exactly-Once Processing

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C104 |
| **Category** | Stream Processing |
| **Difficulty** | 🔴 Hard |
| **Interview Frequency** | 🔥 High |
| **Understanding** | 🔴 None / 🟡 Conceptual / 🟢 Applied |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | 2026-06-01 |
| **Mastery** | 🔴 Familiar / 🟡 Competent / 🟢 Expert |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Exactly-Once Processing guarantees that even in the presence of machine crashes, network partitions, or transient restarts, the internal state of a stream processor and the final side effects in downstream sinks reflect each incoming event exactly once. System architects trigger this pattern when designing high-integrity data pipelines (e.g., payment rails, inventory management, ledgers, and billing engines) where duplicate writes or data loss are unacceptable.
*   **Scalability Dimension:** Primary: **Write/Commit Latency** (due to checkpointing and multi-phase commits). Secondary: **Local Memory/Disk Overhead** (buffering uncommitted transactions) and **Coordination Overhead**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Clarifying the Illusion of "Exactly-Once"
In a distributed system, it is physically impossible to guarantee that a network message is *transmitted* and *executed* by a CPU exactly once. Rather, "exactly-once semantic" means the **net effect on system state is as if the event was processed exactly once**.
To achieve end-to-end exactly-once, the entire pipeline must cooperate:
$$\text{End-to-End Exactly-Once} = \text{Replayable Source} + \text{State Checkpointing Engine} + \text{Idempotent/Transactional Sink}$$

---

### Phase 1: State Checkpointing (Chandy-Lamport Algorithm)
Modern stream processors (like Apache Flink) use a variant of the **Chandy-Lamport algorithm** for asynchronous barrier snapshotting to checkpoint the internal state of operators without stopping the stream.

```
Channel 1 ─── Data ─── Data ─── [Barrier N] ─── Data ────► [ Operator State ]
Channel 2 ─── Data ──────────── [Barrier N] ─────────────► [ (Barrier Align) ]
                                                                   │
                                                                   ▼
                                                            1. Block Channel 1
                                                            2. Wait for Barrier N on Channel 2
                                                            3. Snapshot State to RocksDB/S3
                                                            4. Forward Barrier N Downstream
```

1.  **Checkpoint Barriers:** The Job Manager regularly injects special "checkpoint barriers" into the source streams.
2.  **Barrier Alignment:** When an operator receives Barrier $N$ from one input channel, it pauses reading from that channel and buffers incoming data. It waits until it receives Barrier $N$ from all other input channels.
3.  **Local Snapshot:** Once aligned, the operator saves a snapshot of its local state (e.g., in-memory window sums) to durable storage (e.g., RocksDB or S3).
4.  **Forward:** The operator forwards Barrier $N$ downstream and resumes reading from all input channels.

---

### Phase 2: Transactional Sinks (Two-Phase Commit)
If a node crashes after Flink updates its internal state but before the sink writes to the target database, Flink will roll back to the last checkpoint and replay. If the sink is not transactional or idempotent, duplicate records will be written.

#### Flink's Two-Phase Commit (2PC) protocol with Kafka Sink:
*   **Pre-Commit Phase:**
    1.  When a checkpoint barrier arrives at the Sink operator, Flink begins a checkpoint.
    2.  The Sink writes the current batch of records into a new Kafka transaction (calling `beginTransaction()`).
    3.  The Sink pre-commits the transaction but keeps it open.
    4.  The operator completes its local checkpoint and sends a success acknowledgment to the Job Manager.
*   **Commit Phase:**
    1.  Once the Job Manager receives success acknowledgments for checkpoint $N$ from all operators, it broadcasts a commit signal.
    2.  The Sink receives the signal and commits the Kafka transaction (calling `commit()`), making the written records visible to downstream consumers.
    3.  If any operator fails to checkpoint, the Job Manager broadcasts an abort signal, and the Sink calls `abort()` to roll back the transaction.

```
Coordinator (JobManager)              Sink Operator               External DB / Kafka
         │                                  │                              │
         │─── Ingest Barrier N ────────────►│                              │
         │                                  │─── beginTransaction() ──────►│
         │                                  │─── Write data (pending) ────►│
         │                                  │─── preCommit() ─────────────►│
         │◄── Checkpoint N Success ─────────│                              │
         │                                  │                              │
   [ All nodes OK ]                         │                              │
         │─── Broadcast Commit N ──────────►│                              │
         │                                  │─── commit() ────────────────►│ (Data Visible)
```

---

### Semantics Comparison Table

| Metric | At-Most-Once | At-Least-Once | Exactly-Once (End-to-End) |
| :--- | :--- | :--- | :--- |
| **Data Loss** | ❌ High (events dropped on failure). | ✅ None (retry until delivered). | ✅ None (guaranteed by checkpoint replay). |
| **Duplicates** | ✅ None. | ❌ High (retries create duplicates). | ✅ None (deduplicated or transactional). |
| **Write Latency** | 🟢 Extremely Low. | 🟢 Low (immediate write execution). | ❌ High (events hidden until transaction commits). |
| **Sink Requirements** | None. | None. | Must support Idempotency or 2PC Transactions. |
| **Complexity** | 🟢 Minimal. | 🟡 Medium (retry loops, ack tracking). | 🔴 Extremely High (state coordination, 2PC). |
| **Performance Overhead** | None. | Low. | High (barrier alignment delays, disk I/O). |

---

## 💥 3. Resiliency & Operations

*   **Observability (The "Signal"):**
    *   `Checkpoint Duration / Size`: Sudden spikes in checkpoint times indicate barrier alignment bottlenecks (e.g., slow networks or severe data skew on specific partitions).
    *   `Aborted Transactions Count`: High frequency of aborted sink transactions indicates upstream stream crashes or transient network drops.
    *   `Kafka Transactional State Log Size`: High growth of `__transaction_state` partitions points to stalled or abandoned transactions that are blocking cleanup.
*   **Blast Radius (The "Impact"):**
    *   If a Flink coordinator dies during the commit phase of a 2PC write, the downstream database transaction may remain open indefinitely. This locks database rows or blocks Kafka consumers configured with `isolation.level=read_committed`.
    *   **Mitigation:** Define strict **Transaction Timeouts** (e.g., `transaction.timeout.ms` in Kafka). If a coordinator does not issue a commit within 15 minutes, the broker automatically aborts the transaction to prevent consumer blockage.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Assuming that declaring "exactly-once" in the Kafka producer configuration ensures exactly-once semantics for the whole pipeline. A junior developer misses that if the consumer reads duplicate data and writes it to a non-transactional database like Elasticsearch without specifying document IDs, duplicates will still appear.
*   Failing to explain the latency impact of 2PC. Downstream consumers using `read_committed` cannot read messages as soon as they reach the broker; they must wait until the stream processor completes its checkpoint and commits the transaction (e.g., a 10-second delay).

### Interview Tip (The "Strong Hire" Signal)
> *"For our payment ledger, we realized that idempotency was easier to operate than a full 2-Phase Commit database sink. We designed the stream events with unique UUIDs (a combination of TransactionID + StateChangeCount) and wrote to a PostgreSQL sink using `INSERT ... ON CONFLICT DO UPDATE`. This achieved end-to-end exactly-once semantics without the latency overhead and split-brain risks of 2PC."*

---

## 💡 5. My Custom Study Notes & Whiteboard

### Flink Two-Phase Commit Sink Template (Pseudo-Code)
```java
public class PostgresTransactionalSink 
    extends TwoPhaseCommitSinkFunction<TransactionEvent, Connection, Void> {

    public PostgresTransactionalSink() {
        super(new KryoSerializer<>(Connection.class, new ExecutionConfig()), VoidSerializer.INSTANCE);
    }

    @Override
    protected Connection beginTransaction() throws Exception {
        // Establish connection and turn off auto-commit to start a transaction
        Connection conn = DriverManager.getConnection("jdbc:postgresql://db-host:5432/ledger");
        conn.setAutoCommit(false); 
        return conn;
    }

    @Override
    protected void invoke(Connection conn, TransactionEvent value, Context context) throws Exception {
        // Write values to the active transaction
        String sql = "INSERT INTO transactions (id, amount, status) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value.getId());
            ps.setDouble(2, value.getAmount());
            ps.setString(3, value.getStatus());
            ps.executeUpdate();
        }
    }

    @Override
    protected void preCommit(Connection conn) throws Exception {
        // In PostgreSQL, there is no direct pre-commit command. We prepare the statement.
        // For Kafka, this flushes buffered records to the broker.
    }

    @Override
    protected void commit(Connection conn) throws Exception {
        // Hard commit to make records permanently visible
        conn.commit();
        conn.close();
    }

    @Override
    protected void abort(Connection conn) {
        try {
            // Rollback the uncommitted database transactions
            conn.rollback();
            conn.close();
        } catch (SQLException e) {
            log.error("Failed to abort transaction", e);
        }
    }
}
```

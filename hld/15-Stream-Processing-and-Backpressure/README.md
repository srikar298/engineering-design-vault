# 🌊 15 - Stream Processing & Backpressure

## 📖 1. The Concept
In a 10k+ user system, data doesn't just sit in a DB; it **flows**. Stream processing is the ability to analyze and act on data in real-time as it arrives.

---

## 📊 2. Batch vs. Stream Processing

| Feature | Batch (Hadoop, Spark) | Stream (Kafka Streams, Flink) |
| :--- | :--- | :--- |
| **Latency** | Hours/Days. | Seconds/Milliseconds. |
| **Data Scope** | All data (Finite). | Unbounded (Infinite). |
| **Use Case** | Monthly Payroll, Nightly Reports. | Fraud Detection, Real-time Leaderboards. |

---

## 🛑 3. Flow Control: Backpressure
**The Problem:** The Producer is sending 10,000 events/sec, but the Consumer can only process 1,000/sec. The consumer's memory fills up and it crashes.

**The Solutions:**
1.  **Buffering**: Use a Message Queue (Kafka) to store the events. This buys time but doesn't fix a permanent mismatch.
2.  **Dropping**: Reject or sample incoming data (Load Shedding).
3.  **Backpressure**: The consumer tells the producer to **slow down**.
    *   *TCP Example:* Window size.
    *   *Reactive Streams:* `request(n)` signals.

---

## 🚀 4. The SDE-3 Edge: Exactly-Once Processing
In a distributed stream, how do you ensure an event isn't processed twice (e.g., charging a user twice)?
*   **The Trap:** Most systems are "At-Least-Once".
*   **The Solution:** Use **Idempotency Keys** and **Distributed Snapshots** (Flink approach) or **Transactional Writes** (Kafka approach). Mentioning these specific mechanisms signals high seniority.

# ⚡ 10 - Event Streaming

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C101 |
| **Category** | Messaging & Queues |
| **Difficulty** | 🟡 Medium |
| **Interview Frequency** | 🟡 Medium |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Event Streaming is the continuous, real-time processing and analysis of an ordered, persistent log of events — treating data as an infinite flowing stream rather than batch records to be periodically extracted from a database. Unlike traditional message queues where messages are deleted after consumption, event streams are retained and replayable, enabling stateful stream processing (aggregations, joins, windowed computations) directly on the live data flow.
*   **Scalability Dimension:** Primary: **Real-Time Data Processing at Scale** & **Decoupled Event Replay**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Batch vs Stream Processing
| Dimension | Batch Processing | Stream Processing |
| :--- | :--- | :--- |
| **Data Model** | Process a static dataset at a point in time. | Process events continuously as they arrive. |
| **Latency** | Minutes to hours (scheduled jobs). | Milliseconds to seconds (real-time). |
| **Reprocessing** | Rerun the batch job on historical data. | Replay the event stream from any offset. |
| **Tools** | Hadoop, Spark Batch, dbt. | Kafka Streams, Apache Flink, Spark Streaming, AWS Kinesis. |
| **Use Case** | Nightly billing report, monthly analytics. | Fraud detection, live leaderboards, real-time recommendations. |

### Event Stream Architecture
```
[Sources]           [Stream Processor]          [Sinks]
User clicks  ──►                            ──► Elasticsearch (search index)
Order events ──► [Kafka] ──► [Flink/KStreams] ──► Redis (live counters)
Sensor data  ──►                            ──► Data Warehouse (analytics)
                      │
              Stateful operations:
              - Windowed aggregations (count orders/5min)
              - Stream-to-stream joins (user + order events)
              - Pattern detection (fraud sequences)
```

### Stream Processing Concepts
| Concept | Definition |
| :--- | :--- |
| **Tumbling Window** | Fixed-size, non-overlapping windows. Count orders every 5 minutes (0–5, 5–10, ...). |
| **Sliding Window** | Fixed-size, overlapping windows. Average latency over the last 60 seconds, recalculated every 10s. |
| **Session Window** | Activity-based. A window closes after a user is inactive for X minutes. |
| **Watermark** | A timestamp threshold that tells the processor "no events with timestamp < T will arrive". Allows out-of-order event handling. |

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Stream Processing Lag`: How far behind real-time is the processor reading events?
    *   `Event Skew / Late Arrival Rate`: Events arriving after their watermark → processed in wrong window → incorrect aggregations.
*   **Blast Radius (The "Impact"):**
    *   A stream processor crash mid-window causes in-memory state loss. Production stream processors must checkpoint state to persistent storage (RocksDB in Kafka Streams, state backends in Flink) to recover from failures.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Proposing stream processing for a dashboard that refreshes once per hour (batch is simpler and cheaper).
*   Not knowing that stream processors need **stateful fault tolerance** — checkpointing state so they can resume from the last committed offset after a crash.

### Interview Tip (The "Strong Hire" Signal)
> *"For real-time fraud detection, we use Kafka Streams to join the payment event stream with the user behavior event stream using a sliding 10-minute window. If a user's purchase velocity exceeds 10 transactions in 10 minutes across geographically dispersed locations, we flag the account in <2 seconds of the triggering event."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

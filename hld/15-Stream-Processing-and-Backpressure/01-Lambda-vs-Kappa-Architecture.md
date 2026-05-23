# ⚡ 01 - Lambda vs Kappa Architecture

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C102 |
| **Category** | Stream Processing |
| **Difficulty** | 🟡 Medium |
| **Interview Frequency** | 🟡 Medium |
| **Understanding** | 🔴 None / 🟡 Conceptual / 🟢 Applied |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | 2026-06-01 |
| **Mastery** | 🔴 Familiar / 🟡 Competent / 🟢 Expert |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Lambda and Kappa architectures are patterns designed to ingest and process high-velocity, high-volume data streams while balancing latency and consistency. Architects select Lambda when they need absolute consistency via mathematically exact batch re-computations paired with real-time approximations, whereas they select Kappa to eliminate code duplication by routing all data and replays through a single stream-processing engine.
*   **Scalability Dimension:** Primary: **Data Freshness (Latency) vs. Result Accuracy**. Secondary: **Operational Code Maintainability** and **Storage Read/Write Throughput**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Lambda Architecture Deep Dive
Lambda divides data processing into three distinct layers:
1.  **Batch Layer (Cold Path):** Ingests the immutable master dataset (raw log of events) stored on HDFS/S3. Periodically runs batch jobs (e.g., Apache Spark, MapReduce) to pre-calculate high-accuracy "batch views".
2.  **Speed Layer (Hot Path):** Processes new events in real-time (e.g., Apache Storm, Apache Flink) to update "real-time views". It sacrifices exact correctness (e.g., may miss late-arriving events or use approximate algorithms like HyperLogLog) for ultra-low latency.
3.  **Serving Layer:** Exposes queryable views of both batch and real-time data. When a query arrives, the serving layer merges results from both views to return a comprehensive, near-real-time result.

```
                  ┌──────────────────────┐
                  │  Event Source (Log)  │
                  └──────────┬───────────┘
                             │
            ┌────────────────┴────────────────┐
            ▼ (Async/Batch)                   ▼ (Real-time Streaming)
┌───────────────────────┐         ┌───────────────────────┐
│      Batch Layer      │         │      Speed Layer      │
│ (Spark/HDFS, Exact)   │         │ (Flink, Low Latency)  │
└──────────┬────────────┘         └──────────┬────────────┘
           ▼                                 ▼
┌───────────────────────┐         ┌───────────────────────┐
│      Batch Views      │         │   Real-Time Views     │
└──────────┬────────────┘         └──────────┬────────────┘
           │                                 │
           └────────────────┬────────────────┘
                            ▼
                ┌───────────────────────┐
                │     Serving Layer     │◄── Client Query
                │  (Merge & Serve)      │
                └───────────────────────┘
```

*   **The Code Duplication Problem:** Developers must write and maintain the same business logic twice: once in Java/Scala for Spark batch jobs, and once in another streaming framework (or using different APIs) for real-time processing. This leads to subtle differences in calculations (the "drift" problem) and high maintenance overhead.

---

### Kappa Architecture Deep Dive
Proposed by Jay Kreps, Kappa architecture simplifies the pipeline by removing the batch layer entirely. 
*   **Single Codebase:** All data processing, both historical and real-time, is performed by a single stream processing engine (e.g., Apache Flink, Kafka Streams).
*   **Append-Only Immutable Log:** The historical data is preserved in an ordered, partitionable, immutable log (e.g., Apache Kafka, Apache Pulsar) with high retention or tiered storage (S3/GCS).
*   **Historical Replays:** When business logic changes or a bug is fixed, a new version of the stream processing job is deployed. It is pointed to offset `0` of the immutable log and reads the historical data as a fast-forwarded real-time stream. Once it catches up, the query router swaps the read pointer to the new database view, and the old version of the job and its storage are garbage-collected.

```
                  ┌──────────────────────┐
                  │  Event Source (Log)  │
                  └──────────┬───────────┘
                             │
                             ▼
                ┌────────────────────────┐
                │   Log Storage Broker   │
                │ (Kafka/Pulsar - Long)  │
                └────────────┬───────────┘
                             │
                             ▼
                ┌────────────────────────┐
                │Stream Processing Layer │
                │ (Apache Flink / Spark) │
                └────────────┬───────────┘
                             │
                             ▼
                ┌────────────────────────┐
                │   Serving DB (Views)   │◄── Client Query
                └────────────────────────┘
```

---

### Comparative Evaluation

| Feature | Lambda Architecture | Kappa Architecture |
| :--- | :--- | :--- |
| **Logic Consistency** | ❌ High risk of drift (code written twice for batch & stream). | ✅ Single codebase ensures consistent results. |
| **Operational Overhead** | ❌ High. Requires managing batch frameworks, stream engines, and merge layers. | 🟢 Medium. Requires tuning stream processing states and managing large log retention. |
| **Reprocessing Strategy** | Run historical batch compute jobs over flat raw data on DFS. | Deploy a parallel streaming job, replay the log from offset 0, and switch pointers. |
| **Storage Engine Requirements** | High capacity, low-cost distributed storage (S3, HDFS) + fast real-time DB. | Log brokers with tiered storage support (Kafka Tiered Storage to S3/GCS). |
| **Complex Algorithms** | ✅ Great for complex global optimization / multi-pass graph calculations. | 🟡 Harder to model multi-pass or global structural algorithms in a single pass. |
| **Query Complexity** | ❌ High. The serving layer must merge raw batch and delta real-time results. | 🟢 Low. Queries directly hit the consolidated serving database. |

---

## 💥 3. Resiliency & Operations

*   **Observability (The "Signal"):**
    *   `Log Consumer Lag`: Tracks the difference between the newest offset in Kafka/Pulsar and the consumer group's read offset. A rising lag indicates the stream processor cannot keep up, pointing to GC pauses or bottlenecked downstream database writes.
    *   `Event-Time Drift / Watermark Delay`: Measures how far the internal stream processing watermarks lag behind wall-clock time. Large delays suggest out-of-order data issues.
*   **Blast Radius (The "Impact"):**
    *   In a **Lambda** setup, if the Speed layer crashes, users lose real-time dashboard updates, but the Serving layer can still display the last consistent batch calculations (the "cold" path).
    *   In a **Kappa** setup, a failure in the streaming engine halts all updates. To mitigate this, run a blue-green setup of the streaming jobs across independent compute clusters, allowing zero-downtime deployment and failover.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Stating that Lambda is obsolete. It is still vital when batch-layer operations require non-trivial mathematical optimizations, deep historical clustering, or multi-pass machine learning runs that cannot be computed in a single sequential streaming pass.
*   Assuming Kappa keeps petabytes of data in expensive Kafka broker disks forever. A strong candidate explains **Tiered Storage**, where Kafka automatically offloads closed segment files to cheap object stores (S3/HDFS) while exposing the exact same streaming interface to consumers.

### Interview Tip (The "Strong Hire" Signal)
> *"For our transaction ledger, we chose the Kappa architecture using Apache Flink and Apache Kafka with Tiered Storage to S3. This eliminated the split-brain logic bugs common to Lambda's dual-path codebase. When we modify our ledger validation logic, we deploy a 'v2' Flink job reading from offset 0. Once its checkpointed state caught up to real-time offsets, we did a zero-downtime swap of our API routing layer to the v2 read-optimized database."*

---

## 💡 5. My Custom Study Notes & Whiteboard

### Replay / Blue-Green Migration Protocol in Kappa
```
Step 1: Deploy Job V1 feeding Live DB View V1.
Step 2: Business Logic changes. Deploy Job V2 (V2 Logic) starting from Offset 0.
Step 3: Job V2 writes to DB View V2. Job V2 catches up to real-time stream.
Step 4: Swap API router target from DB View V1 to DB View V2.
Step 5: Decommission Job V1 and DB View V1.
```

### Mock Flink Replay Job Structure (Scala/Java concept)
```java
// Configuration for high-throughput replay
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
env.getConfig().setAutoWatermarkInterval(200);

KafkaSource<TransactionEvent> source = KafkaSource.<TransactionEvent>builder()
    .setBootstrapServers("kafka-cluster:9092")
    .setTopics("transaction-events")
    .setGroupId("analytics-v2-replay")
    .setStartingOffsets(OffsetsInitializer.earliest()) // Crucial for Kappa Replay
    .setValueOnlyDeserializer(new TransactionSchema())
    .build();

DataStream<TransactionEvent> stream = env.fromSource(source, 
    WatermarkStrategy.<TransactionEvent>forBoundedOutOfOrderness(Duration.ofMinutes(5))
    .withTimestampAssigner((event, timestamp) -> event.getTimestamp()), 
    "KafkaSource");

// Apply stateless map and stateful sliding window aggregations
stream.keyBy(TransactionEvent::getMerchantId)
      .window(TumblingEventTimeWindows.of(Time.hours(1)))
      .reduce(new TransactionSumReducer())
      .sinkTo(new PostgresSink("db-view-v2"));

env.execute("Kappa-Replay-Analytics-V2");
```

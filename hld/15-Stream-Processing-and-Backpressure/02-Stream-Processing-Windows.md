# ⚡ 02 - Stream Processing Windows

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C103 |
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
*   **Two-Sentence Trigger:** Stream processing windows partition infinite, unbounded data streams into finite blocks of events based on time or element counts to enable aggregation computations. Architects apply windowing strategies when designing features like real-time fraud detection, rolling click-through-rate (CTR) calculators, or session-based user tracking where metrics must be continuously evaluated over specific temporal ranges.
*   **Scalability Dimension:** Primary: **State Memory Capacity** (storing event state until the window triggers). Secondary: **CPU Utilization** (trigger calculations) and **Disk/Network I/O** (serializing state to external state backends).

---

## ⚖️ 2. Trade-offs & Deep Dive

### Event Time vs. Processing Time vs. Ingestion Time
Understanding time is the foundation of stream windowing:
1.  **Event Time:** The time the event occurred on the client device (embedded in the event payload). This is the only robust model for out-of-order events.
2.  **Ingestion Time:** The time the event was ingested by the storage log broker (e.g., Kafka append timestamp).
3.  **Processing Time:** The local clock time of the stream processing node executing the window. It is highly unstable because a GC pause or network lag will alter when events hit the window, producing incorrect metrics.

---

### Watermarks: Handling Late-Arriving Data
Watermarks are temporal milestones that flow with the stream, indicating the system's progress. A watermark of time $T$ states: *"We assume no more events with an Event Time older than $T$ will arrive."*
*   **Heuristic / Bounded Out-of-Orderness:** If we expect events to arrive at most $D$ seconds out of order, the watermark at real-time progress $t$ is calculated as $W(t) = \max(\text{EventTime}) - D$.
*   When $W(t) \ge \text{Window End Time}$, the engine closes the window, triggers the aggregation logic, and purges the state.

---

### Windowing Strategies

```
Time ─────────►  00:00        00:05        00:10        00:15        00:20
Tumbling:       [  Window 1  ][  Window 2  ][  Window 3  ]
                (Non-overlapping, fixed 5-minute intervals)

Sliding:        [====== Window A ======]
                       [====== Window B ======]
                              [====== Window C ======]
                (Overlapping, 10-minute window sliding every 5 minutes)

Session:        ●─●─●────[Gap]───► ●───[Gap]───► ●─●─●─●───[Gap]───►
                [  Session 1  ]    [S2]          [   Session 3     ]
                (Dynamic size, defined by period of inactivity)
```

1.  **Tumbling Windows:**
    *   Fixed length, non-overlapping.
    *   *Example:* Calculate user pageviews every hour on the hour (10:00-11:00, 11:00-12:00).
2.  **Sliding Windows:**
    *   Fixed length, overlapping.
    *   *Example:* Track the rolling average of server CPU load over the last 10 minutes, updated every 1 minute.
3.  **Session Windows:**
    *   Dynamically sized, defined by gaps of inactivity.
    *   *Example:* Track a user's web session; the window closes after 30 minutes of no user activity. If an event arrives late, it can merge adjacent session windows.
4.  **Global Windows:**
    *   A single window spanning the entire stream. Requires custom triggers and evictors to prevent memory exhaustion (e.g., trigger when 100 events accumulate).

---

### Windowing Comparison Table

| Window Type | Memory Usage | Aggregation Trigger | Best Use Case | Edge Cases |
| :--- | :--- | :--- | :--- | :--- |
| **Tumbling** | 🟢 Low. Events belong to exactly one window. State is purged immediately on close. | When the watermark crosses the fixed boundary. | Hourly sales reports, daily active users (DAU). | Late events are dropped unless "allowed lateness" is set. |
| **Sliding** | ❌ High. Events are copied or referenced in multiple overlapping windows. | At every slide interval (e.g., every minute). | Real-time moving average, alert rules (e.g., 50 errors in last 5 min). | High frequency sliding creates heavy CPU load. |
| **Session** | 🟡 Medium. Requires dynamic state allocation. Can merge windows on late arrivals. | After a period of quiet (no events) exceeds the Gap limit. | E-commerce user funnel conversion analysis. | Out-of-order events force recalculation and window mergers. |
| **Global** | 🔴 Extremely High. Must manually trigger and evict elements. | Custom triggers (e.g., size-based or external signals). | Custom batch-like aggregations over streaming data. | If no trigger fires, memory leaks and crashes the pod. |

---

## 💥 3. Resiliency & Operations

*   **Observability (The "Signal"):**
    *   `Dropped Late Events Rate`: The number of events discarded per second because their event time is older than the closed window watermark. A spike indicates network disruption on client devices.
    *   `State Backend Bytes`: RocksDB memory/disk usage. If storing millions of keys in long sliding windows, monitor this to prevent memory leaks and host OOM (Out Of Memory) states.
*   **Blast Radius (The "Impact"):**
    *   A sudden spike in out-of-order events (e.g., a batch of cached mobile offline logs syncing at once) can trigger excessive state updates.
    *   **Mitigation:** Configure a **Side Output** in Apache Flink/Spark. Instead of dropping late events, route them to a separate Kafka topic for batch reconciliation or DLQ (Dead Letter Queue) processing.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Using Processing Time for financial or billing windows. If the streaming system restarts or lags due to a database lock, all transactions are aggregated into incorrect hourly buckets based on processing time rather than event time, resulting in corrupt records.
*   Failing to account for state size in sliding windows. For example, suggesting a 24-hour sliding window that updates every 1 second over 50,000 requests/sec. Each event must be held in memory across 86,400 overlapping windows, causing instant memory exhaustion.

### Interview Tip (The "Strong Hire" Signal)
> *"For our DDOS detection pipeline, we implemented a 5-minute sliding window with a 10-second slide using Apache Flink. To ensure resilience against mobile clients syncing offline events, we design for event-time watermarking using a Bounded Out-of-Orderness generator of 1 minute. Events arriving after that are routed to a Side Output for separate auditing, keeping the hot path memory footprint lightweight and bounded."*

---

## 💡 5. My Custom Study Notes & Whiteboard

### Flink Event-Time Window with Side Output (Java implementation)
```java
// Define output tag for late events
final OutputTag<TransactionEvent> lateDataTag = new OutputTag<TransactionEvent>("late-transactions"){};

SingleOutputStreamOperator<TransactionSummary> aggregatedStream = sourceStream
    .assignTimestampsAndWatermarks(
        WatermarkStrategy.<TransactionEvent>forBoundedOutOfOrderness(Duration.ofSeconds(10))
        .withTimestampAssigner((event, timestamp) -> event.getTimestamp())
    )
    .keyBy(TransactionEvent::getMerchantId)
    // 1 hour tumbling window based on event-time
    .window(TumblingEventTimeWindows.of(Time.hours(1)))
    // Retain state for late-arriving events for an extra 5 minutes before discarding
    .allowedLateness(Time.minutes(5))
    // Send events that arrive even later to the side output tag
    .sideOutputLateData(lateDataTag)
    .aggregate(new TransactionAggregator());

// Extract and process late data separately
DataStream<TransactionEvent> lateStream = aggregatedStream.getSideOutput(lateDataTag);
lateStream.sinkTo(new KafkaDLQSink());
```

### Watermark Tracking Whiteboard
```
Timeline: Event occurrences vs Stream Processing view
[Source Client] ───► Event(t=12) ───► Event(t=15) ───► (Network Lag) ───► Event(t=9)
                                                                            │
                                                                            ▼
                                                             [Flink Bounded Out-of-Order D=5]
                                                             Current Max Timestamp seen = 15
                                                             Watermark = 15 - 5 = 10
                                                             Is event(t=9) late? Yes (9 < 10)
                                                             Is window [0-10] closed? Yes.
                                                             Outcome: Event(t=9) sent to DLQ/Side Output.
```

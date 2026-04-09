# 🟢 00 - HLD Fundamentals: Requirements & Estimations

Before you draw a single box, you must define what you are building and at what scale.

---

## 🎯 1. Functional vs. Non-Functional Requirements <a name="requirements"></a>

### Functional Requirements (The "What")
These are the specific features the system must provide.
- *Tip:* Pick the **Top 3** features to focus on. Don't try to build the whole app in 45 minutes.
- *Example (Twitter):* User can post tweets, User can follow others, User can see a feed.

### Non-Functional Requirements (The "How")
These define the quality attributes of the system.
- **Scalability**: Can it handle 10M users?
- **Availability**: Is it okay if the service is down for 5 minutes? (99.9% vs 99.999%).
- **Reliability**: Will data be lost if a server crashes?
- **Latency**: How fast must the response be? (e.g., P99 < 200ms).

---

## ⚖️ 2. SLIs, SLOs, and SLAs <a name="availability"></a>

| Metric | Definition | Focus |
| :--- | :--- | :--- |
| **SLI** (Indicator) | The actual metric. | "Latency is 150ms", "Error rate is 0.1%". |
| **SLO** (Objective) | The target goal. | "99% of requests must be < 200ms". |
| **SLA** (Agreement) | The legal contract. | "If SLO is missed, we pay a penalty." |

---

## ⏱️ 3. Latency vs. Throughput <a name="latency"></a>

- **Latency**: The time it takes for a single request to be processed (measured in `ms`).
- **Throughput**: The number of requests processed per unit of time (measured in `RPS`).

### The Latency Budget
A "Strong Hire" candidate calculates how much time each component is allowed to take.
*   **Total Budget**: 500ms
*   **DNS + Network**: 100ms
*   **API Gateway**: 20ms
*   **Service Logic**: 150ms
*   **DB Query**: 100ms
*   **Cache Check**: 5ms
*   **Remaining**: 125ms (Safety buffer)

---

## 🧮 4. Capacity Estimation (Back-of-the-Envelope) <a name="capacity"></a>

### Storage Estimation
...

---

## 🔢 5. Numbers Every Programmer Should Know (DDIA Edition)
To do accurate math in an interview, you must know these approximate latencies:

- **L1 Cache**: 0.5 ns
- **L2 Cache**: 7 ns
- **RAM**: 100 ns
- **SSD Random Read**: 150,000 ns (0.15 ms)
- **Disk Seek (HDD)**: 10,000,000 ns (10 ms)
- **Send packet CA -> Netherlands**: 150 ms

**The Senior Rule:** "Memory is fast, Disk is slow, and the Network is the bottleneck." Use this to justify why we use Redis (RAM) over DB queries (Disk) for high-traffic hot keys.

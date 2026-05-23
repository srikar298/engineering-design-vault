# ⚡ 03 - Latency vs. Throughput

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C002 |
| **Category** | Scalability Fundamentals |
| **Difficulty** | 🟢 Easy |
| **Interview Frequency** | 🔥 High |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Latency is the time taken to process a single request from the client's perspective (measured in milliseconds), while Throughput is the number of requests a system can successfully process within a given time frame (measured in Queries/Requests Per Second). It is triggered when analyzing system performance bottlenecks and capacity tuning, ensuring the system operates below its saturation threshold where queues form and latency explodes.
*   **Scalability Dimension:** Primary: **Concurrency Scaling Limits & Resource Queue Sizing**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Performance Load Curves
As system load (arrival rate) increases, latency and throughput behave non-linearly:
```
Latency vs. Load:                         Throughput vs. Load:
  Latency (ms)                              Throughput (QPS)
    │           / [Saturation Point]          │       /───\ [Max Capacity]
    │          /                              │      /     \
    │_________/                               │_____/       \____ [Thrashing]
    └─────────────────► Load (QPS)            └─────────────────► Load (QPS)
```
1. **Under-Saturated Zone:** Latency is constant and minimal. Throughput scales linearly with incoming requests.
2. **Knee / Saturation Point:** System resources (CPU, threads, I/O) are fully utilized. Queues begin to form.
3. **Over-Saturated Zone:** Latency rises exponentially due to queue waiting time. Throughput plateaus or drops sharply due to thrashing (context switching overhead).

---

### The Mathematics of Concurrency (Little's Law)
To calculate capacity, engineers rely on **Little's Law**:
$$L = \lambda \times W$$
*   **$L$ (Concurrency):** The average number of requests concurrently active inside the system.
*   **$\lambda$ (Throughput):** The arrival rate (requests per second).
*   **$W$ (Latency):** The average time a request spends in the system (latency).

#### Real-World Application (Thread Pool Sizing):
*   Suppose your backend service has a thread pool size of **200** ($L = 200$).
*   Your database query takes **50ms** on average ($W = 0.05\text{ seconds}$).
*   What is the maximum throughput this service can handle before queueing starts?
    $$\lambda = \frac{L}{W} = \frac{200}{0.05} = 4,000 \text{ requests/second}$$
*   If incoming traffic rises to 5,000 requests/sec, the system will run out of threads, requests will queue, and latency will spike.

---

### SDE Slicing & Tuning Strategies
| Strategy | Latency Impact | Throughput Impact | Mechanism |
| :--- | :--- | :--- | :--- |
| **Connection Pooling** | 🟢 Decreases (removes handshake overhead). | 🟢 Increases (reuses active paths). | Bypasses TCP connection setup. |
| **Request Batching** | 🔴 Increases (must wait to form batch). | 🟢 Massive Increase (reduces DB/network transactions). | Aggregates 100 single writes into 1 bulk write. |
| **Caching** | 🟢 Massive Decrease (sub-millisecond RAM read). | 🟢 Massive Increase (bypasses DB bottlenecks). | Reads from Memory (Redis) instead of Disk (DB). |
| **Asynchronous MQs** | 🟢 Decreases (instant HTTP 202 acknowledgment). | 🟢 Increases (absorbs and schedules load). | Offloads heavy tasks to background workers. |

---

## 💥 3. Resiliency & Operations

### Operational Pitfalls & Mitigations
*   **Queue Accumulation (Bufferbloat):**
    *   *Problem:* Under load, requests wait in memory queues. If timeouts are set high, requests wait in queue for 5 seconds before being processed, consuming the entire latency budget before execution.
    *   *Mitigation:* Use bounded queues. Reject requests early (`HTTP 503 Service Unavailable` or `429 Too Many Requests`) if the queue depth exceeds capacity (**Load Shedding**).
*   **Context Switching Overhead:**
    *   *Problem:* Spinning up more OS threads than physical CPU cores causes the CPU to spend more time switching thread contexts than executing code, causing throughput to collapse.
    *   *Mitigation:* Use asynchronous, non-blocking runtimes (Go goroutines, Node.js Event Loop, Java Virtual Threads) to decouple concurrency from physical thread counts.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Believing that latency and throughput are always inversely proportional. You can double throughput (by adding 10 servers) while latency remains exactly the same.
*   Assuming "infinite queue sizes" protect systems. Infinite queues cause memory leaks (OOM) and guarantee timeouts under load.

### Interview Tip (The "Strong Hire" Signal)
> *"We tune our services by balancing latency and throughput limits using Little's Law. For high-volume writes, we trade a small latency penalty (10ms buffering) to batch operations, reducing database I/O bottlenecks. To prevent queue buildup under saturation, we configure bounded request queues and apply active load shedding, returning immediate HTTP 503s rather than allowing queued requests to age out and consume system memory."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

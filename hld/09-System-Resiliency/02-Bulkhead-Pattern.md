# ⚡ 02 - Bulkhead Pattern

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C054 |
| **Category** | System Resiliency |
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
*   **Two-Sentence Trigger:** The Bulkhead Pattern is a resiliency pattern inspired by ship compartmentalization — it isolates different consumers or workload types into separate, fixed-size resource pools (thread pools, connection pools, semaphores) so that the exhaustion of one pool cannot starve resources for other consumers. If Service A overloads its dedicated thread pool, Service B's pool is completely unaffected.
*   **Scalability Dimension:** Primary: **Fault Isolation** & **Resource Contention Prevention**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Two Implementation Styles

**1. Thread Pool Bulkhead (Process Isolation):**
*   Each downstream dependency gets its own fixed-size thread pool.
*   Calls to Service A use Pool A (10 threads). Calls to Service B use Pool B (10 threads).
*   If Service A is slow and its 10 threads are all blocked, Service B calls are unaffected — they use their own 10 threads.
*   *Overhead:* Context switching between thread pools adds latency.

**2. Semaphore Bulkhead (Concurrent Request Limiting):**
*   Each downstream call type has a semaphore limiting concurrent requests.
*   No extra threads created — just a counter of in-flight requests.
*   If the semaphore count is exhausted, new requests are rejected immediately.
*   *Overhead:* Zero thread overhead. Faster, but less isolation than thread pools.

### Without Bulkhead
```
[Shared Thread Pool: 20 threads]
   ├── Service A calls: 18 threads BLOCKED (Service A is slow)
   └── Service B calls: 2 threads available (starvation → timeout)
```

### With Bulkhead
```
[Service A Pool: 10 threads]     [Service B Pool: 10 threads]
   ├── 10 threads BLOCKED            ├── 10 threads healthy
   └── New A calls: REJECTED         └── Serving requests normally ✅
```

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Thread Pool Queue Depth per Bulkhead`: Full queues indicate the bulkhead is sized too small or the downstream is degraded.
    *   `Bulkhead Rejection Rate`: Requests rejected because the pool was full.
*   **Blast Radius (The "Impact"):**
    *   Without Bulkhead: One slow dependency starves the entire application thread pool, causing a complete service outage.
    *   With Bulkhead: Impact is contained to the specific dependency's pool — all other downstream calls continue normally.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Treating Bulkhead and Circuit Breaker as the same pattern — they are complementary. **Bulkhead** limits *concurrent* resource usage. **Circuit Breaker** stops calls to a *failing* service entirely.
*   Sizing all bulkheads equally — high-value, low-traffic dependencies need smaller pools; high-traffic paths need larger ones.

### Interview Tip (The "Strong Hire" Signal)
> *"We isolate our checkout flow from our recommendation service using the Bulkhead pattern with separate thread pools. If recommendations are slow and exhaust their 5-thread pool, checkout's 20-thread pool is completely unaffected. Recommendation calls get rejected fast — we return default recommendations as a fallback."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

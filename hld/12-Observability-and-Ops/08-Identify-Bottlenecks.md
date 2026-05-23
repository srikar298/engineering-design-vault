# ⚡ 08 - Identify Bottlenecks

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C144 |
| **Category** | Observability |
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
*   **Two-Sentence Trigger:** Identifying Bottlenecks is the systematic process of finding the resource constraints (CPU, Memory, Disk, Network, or Lock Contention) that limit a system's throughput and increase its latency. It is triggered during performance regressions, load testing failures, or tail-latency alerts, requiring engineers to analyze execution paths using profiling tools like flame graphs, thread dumps, and database query execution plans.
*   **Scalability Dimension:** Primary: **Application Profiling Overhead vs. Diagnostic Resolution** at scale.

---

## ⚖️ 2. Trade-offs & Deep Dive

### CPU Profile Visualized: The Flame Graph
Flame graphs display stack traces where the x-axis represents the percentage of total CPU time spent in a function (wider = hotter), and the y-axis shows stack depth:
```
  ┌─────────────────────────────────┐
  │   json.Marshal (35% CPU time)   │  ◄── Hot Spot: CPU bottleneck in serialization
  ├─────────────────────────────────┴─────────────┐
  │           processCheckoutOrder (80%)           │
  ├───────────────────────────────────────────────┴────────┐
  │                 http.handleRequest (100%)              │
  └────────────────────────────────────────────────────────┘
```

### Resource Bottleneck Diagnostic Matrix
| Bottleneck Type | Common Cause | Diagnostic Signal / Tool | Remediation Strategy |
| :--- | :--- | :--- | :--- |
| **CPU Saturation** | 1. Heavy JSON parsing.<br>2. Cryptography (SSL/Bcrypt).<br>3. Infinite loops. | Flame Graph (pprof, AsyncProfiler), `top` OS command. | 1. Change to Protobuf/Avro.<br>2. Offload SSL to Gateway.<br>3. Horizontal Scaling. |
| **Disk I/O Bound** | 1. Log writes without buffers.<br>2. Missing database indexes (Table scans). | OS `iostat`, High DB disk queue depth, Slow Query Logs. | 1. Enable batch logging.<br>2. Add DB indexes.<br>3. Move to SSD block storage. |
| **Network I/O Bound** | 1. Chatty microservices (N+1 queries).<br>2. Massive payloads. | Network packet captures (`tcpdump`), High latency in traces. | 1. Use API gateways with batching.<br>2. Compress payloads (gzip/Brotli). |
| **Memory / GC Pauses**| 1. Memory leaks (unreferenced objects).<br>2. High object allocation churn. | Heap Dump (MAT), OS `OOMKilled` event, Long JVM Garbage Collection (GC) pauses. | 1. Fix references.<br>2. Object pooling.<br>3. Tune GC algorithm (G1GC to ZGC). |
| **Lock Contention** | 1. Starved DB connection pool.<br>2. Threads blocked on synchronized blocks. | Thread Dumps, thread state = `BLOCKED` or `TIMED_WAITING`. | 1. Increase DB connections.<br>2. Move to lock-free structures (CAS). |

### Thread Dumps vs. Heap Dumps
To diagnose runtime problems, engineers must select the right diagnostics:
1. **Thread Dump:**
   * *What it is:* A lightweight text snapshot of all active thread stacks.
   * *When to use:* High thread counts, thread starvation, lock deadlocks, or a hanging application.
   * *Production Cost:* 🟢 Very Low (can be run with zero impact).
2. **Heap Dump:**
   * *What it is:* A full binary image of the application's memory heap.
   * *When to use:* Out-of-Memory (OOM) errors, diagnosing memory leaks.
   * *Production Cost:* 🔴 Extremely High (Freezes the JVM process for seconds/minutes; can trigger connection timeout cascade). Never run on an active node—always drain traffic first.

---

## 💥 3. Resiliency & Operations

### Safe Diagnostic Collection Flow
To safely inspect a failing production server:
```
  [ Slow Host Alert ] ──► [ Drain Traffic from Host ] ──► [ Collect Heap Dump / Profile ] ──► [ Restart Node ]
  (Keep instance online)   (Via Load Balancer)             (Zero client-facing impact)        (Recover memory)
```

### Key Metrics to Monitor
*   `GC Pause Duration`: Measures Stop-The-World (STW) pauses. High pause times directly impact tail latency.
*   `DB Connection Pool Wait Time`: If threads wait > 10ms for a connection, the pool is undersized.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Assuming that adding more CPU/RAM is the solution to every bottleneck. If your application is waiting on a database lock, scaling up the application server will not improve performance.
*   Suggesting to run a heap dump directly on a live production server under high load. This will freeze the app, triggering alerts and dropping user requests.

### Interview Tip (The "Strong Hire" Signal)
> *"When identifying bottlenecks, we follow a top-down diagnostic approach. We start by using distributed tracing to isolate the slowest service in the call path. Once isolated, if we see CPU saturation, we capture a Flame Graph using async-profiler to spot CPU hot-spots like heavy JSON parsing. If we suspect a memory leak or OOM issue, we configure our load balancer to drain traffic away from that specific node before triggering a heap dump, preventing client-facing downtime."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

# ⚡ 04 - Distributed Tracing

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C140 |
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
*   **Two-Sentence Trigger:** Distributed Tracing is a specialized diagnostic technique designed to track and profile the end-to-end path of a request as it traverses microservice boundaries, databases, and message queues. It is triggered at the system's entry point (e.g., API Gateway), which stamps the incoming request with a unique global Trace ID and propagates it downstream via HTTP/gRPC metadata headers to construct a Directed Acyclic Graph (DAG) of service-level execution spans.
*   **Scalability Dimension:** Primary: **Trace Ingestion Storage Cost vs. Outlier Coverage (Sampling Strategy)**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Trace Propagation DAG and Spans
When a user hits the endpoint `/checkout`, the call tree is constructed of nested Spans, correlated by a single `Trace ID`:
```
Trace ID: 9f8b8e8a7c6b5a4...
[Span A: API Gateway] (0ms ────────────────────────────────────── 250ms)
   ├── [Span B: Auth Service] (5ms ── 45ms)
   └── [Span C: Order Service] (50ms ──────────────────────── 245ms)
          ├── [Span D: Inventory API] (60ms ── 120ms)
          └── [Span E: DB Write] (130ms ────────────── 240ms)
```

### Context Propagation Wire Protocol (W3C Trace Context)
To track requests across network hops, microservices must pass trace context in protocol headers. The **W3C Trace Context** standard uses the `traceparent` header:
```http
traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01
             │  └─────────────┬──────────────┘ └───────┬──────┘ └─┬┘
          Version         Trace ID                 Span ID      Flags
```
*   **Version (8-bit):** `00` (current standard version).
*   **Trace ID (16-byte hex):** Unique identifier for the entire request path.
*   **Span ID (8-byte hex):** Unique identifier for the current service invocation (parent span ID).
*   **Trace Flags (8-bit):** `01` means "sampled" (record trace), `00` means "do not sample".

### Context Injection vs. Context Extraction
```
[ Service A (Go) ] ──► (HTTP Request + traceparent Header) ──► [ Service B (Node.js) ]
  1. Context Injection:                                          2. Context Extraction:
     Inject active span details into                               Extract traceparent, start
     outgoing HTTP client headers.                                 child span pointing to parent.
```

### Trace Sampling Strategies
Collecting 100% of traces at scale is prohibitively expensive and unnecessary.
1. **Head-based Sampling:**
   * *Mechanism:* The ingestion gateway decides whether to sample the request immediately at the **start** (e.g., exactly 1% of traffic).
   * *Pros:* Simple, requires no buffering, keeps trace volume predictable.
   * *Cons:* Misses critical error traces that happen to fall within the unsampled 99%.
2. **Tail-based Sampling:**
   * *Mechanism:* All spans are recorded and sent to local collector memory buffers. At the **end** of the request lifecycle, the collector decides whether to store it permanently (e.g., save 100% of errors or traces with p95+ latency, but drop 99.9% of simple HTTP 200 requests).
   * *Pros:* Guaranteed capture of anomalies, errors, and high-latency tail events.
   * *Cons:* Highly complex to scale; requires trace collectors to buffer spans in memory and coordinate via hashing to route all spans of a specific Trace ID to the same collector node.

---

## 💥 3. Resiliency & Operations

### Operational Pitfalls & Mitigations
*   **Context Loss in Async Task Handlers:**
    *   *Problem:* When Service A publishes a message to Kafka or spins off an asynchronous thread pool, the trace context is often discarded, causing the trace to break into detached segments.
    *   *Mitigation:* Explicitly inject the trace context into the Kafka/RabbitMQ record headers and extract it on the consumer side before starting the child span.
*   **Performance Overhead of Instrumentation:**
    *   *Problem:* Frequent timestamp querying (calling OS `clock_gettime`) and serializing tracing objects on every function call consumes substantial CPU cycles.
    *   *Mitigation:* Trace at microservice boundaries (HTTP, gRPC, DB queries) rather than instrumenting internal functions (methods), and use non-blocking background threads to flush tracing spans in batches.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Assuming tracing requires manual context forwarding through every business logic function. Modern libraries (OpenTelemetry) use thread-local storage (JVM MDC, AsyncLocalStorage in Node.js) to manage active span context automatically.
*   Suggesting 100% trace capture in a high-traffic system (100k+ RPS) without acknowledging the massive disk-writes, network egress, and cloud bill consequences.

### Interview Tip (The "Strong Hire" Signal)
> *"In our distributed microservice mesh, we implement OpenTelemetry for distributed tracing. To capture critical system anomalies while keeping cloud storage costs low, we run an OpenTelemetry Collector cluster using Tail-based Sampling. We buffer spans in collector memory and route them using trace-ID consistent hashing, saving 100% of traces containing HTTP 5xx responses or database spans exceeding 200ms, while discarding 99.9% of healthy HTTP 200 traces."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

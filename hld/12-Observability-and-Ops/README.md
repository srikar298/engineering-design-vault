# 📡 12 - Observability & Ops (SDE-2+ Excellence)

You can't fix what you can't measure. Observability is about understanding the internal state of a system by looking at its outputs.

---

## 📊 1. The Three Pillars

| Pillar | Purpose | Tools |
| :--- | :--- | :--- |
| **Metrics** | Numerical data over time (aggregatable). | Prometheus, Grafana, Datadog. |
| **Logging** | Discrete events (textual). | ELK Stack (Elastic, Logstash, Kibana), Loki. |
| **Tracing** | Following a request across service boundaries. | Jaeger, Zipkin, AWS X-Ray. |

---

## ⚖️ 2. SLIs, SLOs, and SLAs

- **SLI (Service Level Indicator)**: The actual metric (e.g., "Latency is 150ms").
- **SLO (Service Level Objective)**: The target goal (e.g., "99% of requests must be < 200ms").
- **SLA (Service Level Agreement)**: The legal contract (e.g., "If availability drops below 99.9%, we refund the customer").

---

## 🚨 3. Distributed Tracing: The Visibility Engine

In a 10k+ user system, a single request might hit 10 different services. How do you find the slow one?

```mermaid
sequenceDiagram
    participant C as Client
    participant G as API Gateway (TraceID: 123)
    participant A as Auth Service (TraceID: 123)
    participant D as Database (TraceID: 123)
    
    C->>G: Request
    G->>G: Create TraceID: 123
    G->>A: Validate User (TraceID: 123)
    A->>A: Log ProcessTime (SpanID: A_1)
    A-->>G: Success
    G->>D: Get Data (TraceID: 123)
    D->>D: Log QueryTime (SpanID: D_1)
    D-->>G: Data
    G-->>C: 200 OK
```

### The SDE-3 Edge: Sampling Strategies
At scale (1M+ requests), you cannot trace *everything* because the storage cost would exceed the value.
*   **Head-based Sampling:** Decide to trace a request at the **start** (e.g., sample 1%). Easiest to implement.
*   **Tail-based Sampling:** Trace everything in memory, but only **save** the traces that take > 200ms or return an error. **The Senior Choice** for finding bugs while keeping costs low.

---

## 🔔 4. Alerting & On-Call Excellence

- **The Trap:** Alerting on **Causes** (e.g., "CPU > 80%"). This leads to "Alert Fatigue" and ignored pages.
- **The Gold Standard:** Alert on **Symptoms** (e.g., "P99 Latency > 500ms" or "HTTP 5xx error rate > 1%"). These are the only things that directly impact your users.

**Senior Signal:** "We follow the **RED Pattern** for our microservices: **R**ate (Requests/sec), **E**rrors (Failed requests/sec), and **D**uration (Latency distribution). We only page engineers when these high-level signals deviate from our SLOs."

---

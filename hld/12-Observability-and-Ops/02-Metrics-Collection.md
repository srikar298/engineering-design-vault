# ⚡ 02 - Metrics Collection

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C138 |
| **Category** | Observability |
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
*   **Two-Sentence Trigger:** Metrics Collection is the systematic process of gathering numeric time-series measurements representing system performance, health, and throughput at regular intervals. It is triggered continuously across all running nodes, utilizing either a pull model (where a monitoring server pulls metrics from target HTTP endpoints) or a push model (where application processes actively transmit UDP/TCP packets to a central collector).
*   **Scalability Dimension:** Primary: **Ingestion Throughput vs. Storage Footprint** at millions of data points per second.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Pull vs. Push Architectural Models
```
Pull Architecture (e.g., Prometheus):
  [ TSDB / Scraper ] ─────── HTTP GET /metrics ───────► [ App Endpoint ] (Exposes state)
  (Centralized scheduler, server-controlled load)

Push Architecture (e.g., StatsD / Datadog Agent):
  [ App Code ] ─────── UDP (Fire-and-forget) ───────► [ Daemon / Collector ]
  (Decentralized, application-controlled traffic)
```

### Pull vs. Push Comparison
| Feature | Pull Model (e.g., Prometheus) | Push Model (e.g., StatsD) |
| :--- | :--- | :--- |
| **Data Flow** | Server initiates scraping at a set frequency. | Client pushes data continuously. |
| **Transport** | TCP (HTTP/HTTPS) - reliable. | UDP - lightweight, fast, potentially lossy. |
| **Discovery** | Centralized Service Discovery (Kubernetes API, Consul). | Client needs to know the collector's IP/DNS. |
| **Overload Risk** | 🟢 Low: Server stops scraping if it is overloaded. | 🔴 High: Spikes in app traffic flood the collector. |
| **App Overhead** | High serialization cost on scrape request. | Very low (UDP socket write is non-blocking). |
| **Security** | Targets must expose public/internal HTTP port. | Targets require no inbound ports; outbound only. |

### Core Metric Types
1. **Counter:** A cumulative metric that only increases (or resets to 0 on restart). Used for total requests, errors, or bytes processed.
2. **Gauge:** A single numerical value that can go up and down arbitrarily. Used for memory usage, CPU load, temperature, or queue length.
3. **Histogram:** Measures the distribution of values (typically durations or sizes) into pre-configured buckets. Allows server-side calculation of percentiles (e.g., p99 latency) across multiple instances.
4. **Summary:** Like a histogram, but calculates percentiles (e.g., p95, p99) client-side over a sliding time window. Cannot be aggregated across multiple hosts, making them less useful in distributed systems.

### Monitoring Methodologies
*   **The USE Method (Infrastructure Focus):** Used for resources (CPUs, disks, memory).
    *   *U*tilization: How busy the resource is (e.g., CPU load %).
    *   *S*aturation: The degree of queued, backlogged work (e.g., OS run queue length).
    *   *E*rrors: The count of error events (e.g., disk read failures).
*   **The RED Method (Request Focus):** Used for microservices.
    *   *R*ate: Number of requests per second.
    *   *E*rrors: Number of failed requests per second.
    *   *D*uration: Time taken to process requests (latency distribution).

---

## 💥 3. Resiliency & Operations

### Operational Pitfalls & Mitigations
*   **Scrape Timeouts & Memory Overhead:**
    *   *Problem:* If an application exposes millions of metrics (due to cardinality issues) or is under heavy lock contention, serializing the `/metrics` endpoint string over HTTP will time out. This consumes massive heap memory, triggering Java garbage collection pauses or Node.js event-loop delays.
    *   *Mitigation:* Keep metric label cardinality low, use Prometheus native OpenMetrics protobuf format instead of text representation, and implement client-side caching of serialized metrics with a 1-second TTL.
*   **StatsD UDP Packet Loss:**
    *   *Problem:* High-throughput push models over UDP drop packets silently during network congestion, leading to inaccurate count metrics.
    *   *Mitigation:* Deploy a local StatsD agent (daemonset/sidecar) on the same host/pod via `localhost` (UDP drops are near zero), which then forwards metrics to the central cluster reliably via TCP.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Creating a tag/label for `user_id` or `order_id` in a metric. If you have 1,000,000 users, Prometheus creates 1,000,000 timeseries streams. This is called a **cardinality explosion** and will crash the TSDB server due to memory exhaustion.
*   Suggesting summaries are better than histograms. Inform the interviewer that summaries cannot be aggregated (you cannot average p99s across 10 pods mathematically), whereas histograms allow computing global percentiles via `histogram_quantile()`.

### Interview Tip (The "Strong Hire" Signal)
> *"For our microservice mesh, we use a pull-based Prometheus model. To prevent CPU spikes on the application during scrape requests, we use local sidecars that cache metrics outputs. We enforce strict linting to prevent cardinality explosions, forbidding dynamic values like user IDs or transaction hashes from being used as labels. For those high-cardinality diagnostics, we route them to our distributed tracing and logging stack instead."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

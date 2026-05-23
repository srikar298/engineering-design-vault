# ⚡ 05 - Timeouts

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C051 |
| **Category** | System Resiliency |
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
*   **Two-Sentence Trigger:** A Timeout is a hard deadline applied to any network call, database query, or blocking operation — if no response arrives within the configured duration, the call is abandoned and an error is returned immediately. Without timeouts, a single slow downstream dependency can hold threads open indefinitely, exhausting thread pools and causing cascading failures across the entire system.
*   **Scalability Dimension:** Primary: **Thread Pool Protection** & **Cascading Failure Prevention**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Types of Timeouts You Must Configure
| Timeout Type | What It Controls | Example Value |
| :--- | :--- | :--- |
| **Connection Timeout** | Max time to establish a TCP connection to the downstream server. | 1–3 seconds |
| **Read/Socket Timeout** | Max time to wait for data **after** connection is established. | 5–30 seconds (depends on operation) |
| **Write Timeout** | Max time to complete sending a request payload. | 5–10 seconds |
| **Request Timeout** | Total end-to-end timeout for the entire request lifecycle. | 10–60 seconds |
| **Idle Connection Timeout** | How long a pooled connection can sit unused before being closed. | 30–90 seconds |

### The Timeout Propagation Problem
In a chain of services (A → B → C → D), each hop adds its own processing time. If **each service sets a 5-second timeout**, the effective deadline shrinks:
*   A waits up to 5s total.
*   B waits up to 5s (but A only waits 5s total — B's timeout is irrelevant if A gives up first).
*   **Critical Bug:** If A times out at 5s but B continues processing for another 10s, B wastes resources on a request whose result no one wants.

**Solution: Deadline Propagation (Context Deadlines)**
*   Pass the **remaining deadline** as a gRPC deadline or HTTP header (`X-Request-Deadline`).
*   Each service checks: "Does the caller still have time left to receive my response?"
*   gRPC natively supports this — the deadline propagates through the entire call chain.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Timeout Rate per Downstream Dependency`: Spikes indicate a slow downstream or undersized timeout.
    *   `P99 Request Duration vs Timeout Threshold`: If P99 is close to the timeout, increase the timeout OR fix the downstream slowness.
*   **Blast Radius (The "Impact"):**
    *   **No Timeout:** One slow dependency holds threads indefinitely → thread pool exhaustion → entire service becomes unresponsive → cascading failure.
    *   **Too-Short Timeout:** Healthy requests are incorrectly failed, degrading user experience unnecessarily.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Not knowing the distinction between **Connection Timeout** and **Read Timeout** (TCP can connect successfully to a server that then hangs on processing — Connection Timeout doesn't catch this; Read Timeout does).
*   Setting the same timeout for all operations (a health check deserves 500ms; a report generation endpoint deserves 30s).
*   Not propagating deadlines across service boundaries — the upstream times out but the downstream wastes compute.

### Interview Tip (The "Strong Hire" Signal)
> *"We configure three timeout values per downstream client: 1s connection timeout, 5s read timeout, and 10s overall request budget. We propagate the remaining deadline to all downstream gRPC calls. If a request has already consumed 9 of its 10 seconds, the downstream service sees a 1s gRPC deadline and can short-circuit immediately rather than doing expensive work for a caller that's about to time out."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

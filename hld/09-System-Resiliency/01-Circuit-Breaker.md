# ⚡ 01 - Circuit Breaker

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C053 |
| **Category** | System Resiliency |
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
*   **Two-Sentence Trigger:** A Circuit Breaker is a resiliency pattern that wraps outbound calls to a downstream service, monitoring failure rates, and "opening" the circuit to short-circuit all calls (fast-fail) when failures exceed a threshold. After a configured recovery period, it transitions to "Half-Open", testing whether the downstream service has recovered before resuming normal traffic.
*   **Scalability Dimension:** Primary: **Cascading Failure Prevention** & **Thread Pool Protection**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### The Three-State Machine
```
┌─────────────────────────────────────────────────────┐
│                                                     │
│   CLOSED ──(failures > threshold)──► OPEN           │
│     ▲                                  │            │
│     │                          (wait timeout)       │
│     │                                  │            │
│     └──(probe succeeds)── HALF-OPEN ◄──┘            │
│                               │                     │
│                      (probe fails)                  │
│                               └──────────► OPEN     │
└─────────────────────────────────────────────────────┘
```

*   **CLOSED (Normal):** Requests flow to the downstream service. Failures are counted in a rolling window.
*   **OPEN (Failing):** Downstream is detected as failing. All calls **immediately return an error or fallback** — no network call is attempted. Thread pools are protected.
*   **HALF-OPEN (Recovering):** After a configured wait (e.g., 30s), the breaker allows a small probe of requests through. If they succeed, transition back to CLOSED. If they fail, return to OPEN.

### Configuration Parameters (The "Numbers")
| Parameter | Typical Value | What It Controls |
| :--- | :--- | :--- |
| `failure_threshold` | 50% in last 20 requests | When to open the breaker. |
| `open_timeout` | 30–60 seconds | How long to stay open before probing. |
| `half_open_probes` | 5 requests | How many test requests in half-open state. |
| `success_threshold` | 3 successes | Required successes to close from half-open. |

### The Cascading Failure Without Circuit Breaker
Without a Circuit Breaker, Service A calls Service B (which is slow/down):
1. Service A's threads block waiting for Service B to respond.
2. Incoming requests to Service A queue up, all blocked on Service B calls.
3. Service A's thread pool exhausts — it becomes unresponsive.
4. Service C (which calls A) also blocks → cascading crash.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Circuit Breaker State (OPEN/CLOSED/HALF-OPEN)`: An OPEN state should immediately trigger an alert.
    *   `Fast-Fail Rate`: Requests rejected by the open circuit (not actual downstream failures).
*   **Blast Radius (The "Impact"):**
    *   With Circuit Breaker: Downstream failure is isolated. Service A fails fast and degrades gracefully.
    *   Without Circuit Breaker: Thread pool exhaustion cascades to all callers of Service A.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Confusing Circuit Breaker with Retry — they solve opposite problems. **Retry** handles transient failures (retry for success). **Circuit Breaker** handles sustained failures (stop retrying to protect the caller).
*   Not providing a **fallback** when the circuit is open (e.g., return cached data, a default value, or a graceful error message — not a raw connection exception).

### Interview Tip (The "Strong Hire" Signal)
> *"When our Payment Service experiences latency spikes, we use a Circuit Breaker (via Resilience4j) with a 50% failure threshold over 20 requests. When OPEN, we fast-fail and return cached payment method data as a fallback. This prevents the Payment Service's slowness from exhausting checkout threads and cascading to the entire checkout flow."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

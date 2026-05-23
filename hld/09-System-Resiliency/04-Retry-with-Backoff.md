# ⚡ 04 - Retry with Backoff

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C052 |
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
*   **Two-Sentence Trigger:** Retry with Backoff is a resiliency pattern where a failed network call is automatically retried after an increasing delay (backoff), rather than immediately — giving the downstream service time to recover. Without backoff, a flood of simultaneous retries from all callers can amplify the outage (a "retry storm"), preventing the downstream from recovering.
*   **Scalability Dimension:** Primary: **Transient Failure Tolerance** & **Retry Storm Prevention**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Backoff Strategies
| Strategy | Formula | Effect |
| :--- | :--- | :--- |
| **Fixed Delay** | Wait $d$ ms between retries (e.g., 500ms always). | Simple. Still causes thundering herds if all clients use same $d$. |
| **Linear Backoff** | Wait $d \times \text{attempt}$ (e.g., 500ms, 1000ms, 1500ms). | Progressively less aggressive. Still synchronized across clients. |
| **Exponential Backoff** | Wait $d \times 2^{\text{attempt}}$ (e.g., 500ms, 1000ms, 2000ms, 4000ms). | Rapid early spacing, then spreads retries. Industry standard. |
| **Exponential Backoff + Jitter** | Add random jitter: wait $\text{random}(0, d \times 2^{\text{attempt}})$. | **Desynchronizes** retries across all clients. Prevents thundering herds. ✅ |

### The Retry Storm Problem (Without Jitter)
```
t=0s:  All 1000 clients get 503 from Service B
t=0.5s: All 1000 clients retry simultaneously → 1000 requests hit B again
t=1.0s: B still overloaded → another 1000 retry → amplified load
Result: B can never recover because retries keep hitting it in coordinated waves.
```

### With Exponential Backoff + Jitter
```
t=0s:   All 1000 clients fail
t=0.1–1.0s: Random first retries spread across a 1-second window
t=0.5–4.0s: Second retries spread across a wider window
Result: Load is spread evenly → B gradually recovers under manageable traffic.
```

### What to Retry — and What NOT to Retry
*   ✅ **Retry:** `503 Service Unavailable`, `429 Too Many Requests` (with Retry-After header), `504 Gateway Timeout`, network connection errors.
*   ❌ **Never Retry:** `400 Bad Request` (your fault — same request will always fail), `401 Unauthorized`, `403 Forbidden`, `404 Not Found`. Retrying these wastes resources and adds load.
*   ⚠️ **Idempotency Required:** Only retry requests that are **idempotent**. Retrying a `POST /payments` non-idempotently will double-charge the user.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Retry Count per Request`: High retry counts on specific endpoints indicate persistent downstream degradation.
    *   `Retry Budget Exhaustion Rate`.
*   **Blast Radius (The "Impact"):**
    *   Aggressive retries without jitter during a partial outage can amplify failure into a full cascading outage across the entire system.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Retrying `4xx` errors (client errors are deterministic — retrying wastes resources).
*   Not adding jitter (the most common real-world mistake that causes thundering herds during incidents).
*   Not combining Retry with a **max retry budget** (e.g., max 3 retries) and a **Circuit Breaker** to stop retrying against a sustainably-down service.

### Interview Tip (The "Strong Hire" Signal)
> *"Our HTTP client uses **Exponential Backoff with Full Jitter** for `503` and `504` errors — initial delay of 100ms, max delay of 30s, max 3 retries. Jitter prevents retry storms. We also wrap all retried calls in a Circuit Breaker — if the error rate stays above 50%, we stop retrying entirely and fast-fail."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

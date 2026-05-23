# ⚡ 06 - Latency Budgets & Percentiles

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C142 |
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
*   **Two-Sentence Trigger:** A Latency Budget is the maximum amount of time allocated to a distributed request path, divided and enforced among downstream microservices, queries, and network hops to satisfy a global client-side SLO. It is triggered during microservice architectural design and API routing configuration, defining strict timeout limits, fallback paths, and parallel execution constraints for every hop in the call tree.
*   **Scalability Dimension:** Primary: **Tail Latency Mitigation & Thread Pool Resiliency** at high concurrent load.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Latency Budget Allocation Tree
A global client budget of `250ms` is decomposed and enforced along a microservice call tree:
```
  [ Client Request ] ─────── (Global Budget: 250ms) ───────► [ API Gateway ]
                                                                │
                            ┌───────────────────────────────────┴───────────────────────────────────┐
                  [ Auth (Timeout: 30ms) ]                                               [ Orders (Timeout: 180ms) ]
                  Reads token from cache (15ms)                                            │
                                                                    ┌──────────────────────┴──────────────────────┐
                                                          [ DB Read (Timeout: 80ms) ]         [ Payment (Timeout: 90ms) ]
                                                          Succeeds in 35ms                    Slow downstream call (90ms Timeout)
                                                                                              -> Triggers fallback cache / failure
```

### Tail Latency Amplification: The Math of Averages
Many engineers incorrectly measure performance using "Average Latency." Averages mask outliers. 
*   **Percentiles Explained:**
    *   **p50 (Median):** 50% of requests are faster than this.
    *   **p99:** 99% of requests are faster; 1 in 100 users experiences this slow response.
    *   **p99.9:** 99.9% of requests are faster; 1 in 1,000 users experience this (usually heavy buyers or complex database queries).
*   **Tail Latency Amplification (The "Multi-Service Trap"):**
    *   Suppose a single service has a p99 latency of `1 second` (meaning 1% of calls take 1s).
    *   If your gateway calls **100** independent instances of this service in parallel to serve a single user page, the probability that your user experiences a 1-second delay is:
        $$\text{Probability} = 1 - (0.99^{100}) \approx 63.4\%$$
    *   Even though every single service is healthy (99% normal), 63% of your users experience the tail latency!

### Mitigation Strategies
1. **Tight, Dynamic Timeouts:**
   * Do not hardcode static timeouts. As a request flows down the stack, pass the "remaining budget" in the header (e.g., `X-Request-Deadline`). Downstream services read this header and adjust their timeouts dynamically.
2. **Hedged Requests (Taming Tail Latency):**
   * Send the request to Replica A. If Replica A does not respond within the p95 latency threshold (e.g., 20ms), immediately spin up a duplicate request to Replica B in parallel. Process whichever response returns first, cancelling the other.
3. **Graceful Degradation & Fallbacks:**
   * If the recommendation service fails to respond within its 50ms budget, bypass it. Render the UI page using a static list of popular products instead of throwing an error.

---

## 💥 3. Resiliency & Operations

### Operational Pitfalls & Mitigations
*   **Unbounded Outbound Timeouts (Cascading Failure):**
    *   *Problem:* If Service A calls Service B with no timeout, and Service B hangs (due to a DB deadlock), Service A keeps its thread blocked. As new requests arrive, Service A's thread pool is completely exhausted, causing the gateway to return `504 Gateway Timeout` for all other traffic.
    *   *Mitigation:* Never use default/infinite client timeouts. Enforce short timeouts (p99 of downstream service + 20% buffer) on all HTTP/gRPC clients.
*   **Queue Delays (Bufferbloat):**
    *   *Problem:* Setting high timeouts allows long queues of requests to build up under load. The queue time itself consumes the entire latency budget before the request is even processed.
    *   *Mitigation:* Use bounded queues and drop requests early if they have already exceeded their deadline (Dead-line propagation checks).

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Stating that system performance is "perfect" because the "average latency is 50ms." Averages hide major errors and slow paths.
*   Suggesting to fix slow requests by adding retries immediately without exponential backoff or budgets—this acts as a self-inflicted DDoS.

### Interview Tip (The "Strong Hire" Signal)
> *"To mitigate tail latency amplification in our microservice architecture, we enforce dynamic deadline propagation. The API Gateway injects a deadline timestamp in the request context. Each downstream microservice calculates its remaining budget before making outbound calls. If a service like the recommendation engine runs out of budget, it returns a static fallback response in 10ms, preserving the global user checkout latency budget."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

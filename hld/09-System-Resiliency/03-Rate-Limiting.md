# ⚡ 03 - Rate Limiting

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C055 |
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
*   **Two-Sentence Trigger:** Rate Limiting is a resiliency and protection mechanism that restricts how many requests a client, user, or API key can make within a defined time window, returning `429 Too Many Requests` when the limit is exceeded. It protects backend services from traffic spikes, abusive clients, DDoS attacks, and resource starvation.
*   **Scalability Dimension:** Primary: **System Protection / Fair Resource Allocation**. Secondary: **API Monetization (tier-based quotas)**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Rate Limiting Algorithms
| Algorithm | How It Works | Pros | Cons |
| :--- | :--- | :--- | :--- |
| **Fixed Window Counter** | Count requests in a fixed time window (e.g., 0–60s). Reset counter at window boundary. | Simple. $O(1)$ storage per key. | **Boundary burst**: 100 req in the last second of window + 100 in first second of next = 200 req in 2 seconds. |
| **Sliding Window Log** | Store timestamp of every request in a sorted log. Count requests in the rolling last 60s. | Perfectly smooth — no boundary burst. | $O(\text{limit})$ storage — every request logged. |
| **Sliding Window Counter** | Hybrid: Uses current + previous window counter with a weighted interpolation formula. | Near-perfect accuracy. $O(1)$ storage. | Slightly approximate (not exact). |
| **Token Bucket** | A bucket holds tokens (capacity = burst limit). Each request consumes 1 token. Tokens refill at a constant rate. | Allows **controlled bursting** above steady-state rate. | Requires synchronized token state across distributed nodes. |
| **Leaky Bucket** | Requests enter a FIFO queue (the bucket) and are processed at a fixed constant rate. Overflow is dropped. | Perfectly smooth output rate — no bursts allowed. | Latency added — requests queue rather than being processed immediately. |

### Distributed Rate Limiting
In a multi-server cluster, each server cannot independently track counts — clients could spread requests across servers to bypass limits.
*   **Centralized Counter (Redis):** All API servers share a Redis counter using `INCR` + `EXPIRE`. Atomic operations prevent race conditions.
    *   *Trade-off:* Redis becomes a hot-path dependency. Latency impact per request.
*   **Local Approximate Limiting:** Each server maintains local counts. Periodically sync to a central store. Allows some burst above the limit during sync gaps.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `429 Response Rate`: Sudden spikes indicate client bugs, credential stuffing, or traffic storms.
    *   `Redis Rate Limit Counter Latency`: Milliseconds added per request for the limit check.
*   **Blast Radius (The "Impact"):**
    *   If the rate limiting Redis cluster fails and rate limiting falls back to "allow all", a DDoS or rogue client can take down backend services.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Proposing Fixed Window Counter without mentioning the boundary burst problem (interviewers specifically test this).
*   Not differentiating between **rate limiting** (protect the system from one client) vs **throttling** (protect the system from all clients under overload — shed load globally).

### Interview Tip (The "Strong Hire" Signal)
> *"We use the **Token Bucket** algorithm via Redis. Tokens refill at our steady-state rate (100 req/s per API key) but the bucket allows a burst of 150 tokens. This lets legitimate clients handle short spikes without being immediately rejected, while still bounding long-term throughput."*

---

## 💡 5. My Custom Study Notes & Whiteboard
```
Token Bucket Visualized:
Bucket Capacity: 10 tokens (max burst)
Refill Rate: 2 tokens/second

t=0s:  [●●●●●●●●●●] 10 tokens
t=1s:  Send 5 requests → [●●●●●] 5 tokens remaining, +2 refill → [●●●●●●●] 7 tokens
t=2s:  Send 8 requests → [●●●●●●●] - 7 = 0, 1 request REJECTED (429)
```

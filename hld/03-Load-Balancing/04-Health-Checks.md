# ⚡ 04 - Health Checks

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C046 |
| **Category** | Load Balancing |
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
*   **Two-Sentence Trigger:** Health Checks are periodic probes sent by a load balancer or orchestrator to backend servers to determine if they are capable of handling traffic. A failing health check causes the LB to remove that server from the routing pool, preventing requests from being sent to a crashed or degraded instance.
*   **Scalability Dimension:** Primary: **High Availability** & **Failure Detection Latency**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Active vs Passive Health Checks
| Type | Mechanism | Detection Speed | Overhead |
| :--- | :--- | :--- | :--- |
| **Active (Synthetic)** | LB periodically sends a probe request (TCP ping or HTTP GET `/health`) to each backend on a fixed interval (e.g., every 5s). | Fast — configured interval. | Consumes server resources even during idle periods. |
| **Passive (Reactive)** | LB observes real traffic responses. If $K$ consecutive requests to a backend return 5xx or timeout, it marks the server down. | Slower — requires $K$ real client failures first. | Zero overhead — piggybacks on existing traffic. |

**Best Practice:** Use **both** — active checks for early detection during low-traffic windows, passive checks to catch gradual degradation under live traffic.

### The Shallow vs Deep Health Check
*   **Shallow (Basic):** `GET /health` returns `200 OK` as long as the HTTP server process is alive.
    *   *Trap:* The server is alive but its database connection is dead. The LB thinks it's healthy and routes traffic. All requests fail at the DB layer.
*   **Deep (Composite):** `GET /health` internally verifies:
    1. Database connectivity (`SELECT 1`).
    2. Cache connectivity (Redis ping).
    3. Dependency service reachability (downstream API `/ping`).
    *   *Returns:* `200 OK` only if ALL dependencies are healthy. Returns `503 Service Unavailable` with a JSON body describing which dependency failed.

```json
// Deep health check response example
{
  "status": "degraded",
  "checks": {
    "database": "ok",
    "redis": "ok",
    "payment_service": "timeout"  // ← LB removes this instance from pool
  }
}
```

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Health Check Failure Rate per Backend`: Spikes indicate a degraded deployment or config change.
    *   `Time-to-Detect (TTD)`: How long between a server crash and LB removing it from rotation.
*   **Blast Radius (The "Impact"):**
    *   **Zombie Servers:** Shallow health checks allow a partially-broken server to stay in rotation, silently failing a percentage of user requests with no obvious LB-level signal.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Implementing a health check that always returns `200 OK` unconditionally (defeats the entire purpose).
*   Not accounting for **flapping** — a server alternating between healthy and unhealthy rapidly, causing the LB to route to it and then remove it in cycles. *Fix:* Require $K$ consecutive successes before marking healthy again (hysteresis).

### Interview Tip (The "Strong Hire" Signal)
> *"Our `/health` endpoint is a deep composite check. It verifies DB pool connectivity, Redis reachability, and downstream service dependencies. A `503` triggers immediate LB pool removal. We use hysteresis — 3 consecutive failures to remove, 5 consecutive successes to re-add — preventing flapping from intermittent network blips."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

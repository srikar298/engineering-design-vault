# ⚡ 02 - Load Balancing Algorithms

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C044 |
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
*   **Two-Sentence Trigger:** Load Balancing Algorithms determine *which* backend server receives each incoming request. Choosing the wrong algorithm for your traffic pattern causes hot-spot servers, degraded user experience, and wasted capacity on idle machines.
*   **Scalability Dimension:** Primary: **Request Throughput Distribution** & **Backend Server Utilization**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Algorithm Comparison Table
| Algorithm | How It Works | Best For | The Trap |
| :--- | :--- | :--- | :--- |
| **Round Robin** | Requests distributed cyclically: Server 1, 2, 3, 1, 2, 3... | Homogeneous servers, uniform short requests (e.g., static assets). | Ignores server load — a slow server keeps receiving requests even while processing many. |
| **Weighted Round Robin** | Servers assigned weights based on capacity. A server with weight 3 gets 3× more requests. | Mixed-capacity server fleets (e.g., some 8-core, some 16-core). | Weights are static — cannot adapt to real-time load changes. |
| **Least Connections** | Routes to the server with the fewest active open connections. | Long-lived connections: WebSockets, gRPC streams, DB connections. | Requires the LB to track state — does not scale trivially to millions of connections. |
| **Least Response Time** | Routes to the server with the lowest average response latency AND fewest connections combined. | Latency-sensitive APIs where backends can vary in speed. | Requires continuous latency sampling from all backends (extra overhead). |
| **IP Hash (Sticky)** | `hash(client_IP) % N` — same client always routes to the same server. | Session-based stateful apps (e.g., WebSocket chat, game servers). | If the hashed server crashes, all that client's sessions are lost. |
| **Random** | Route to a random healthy server. | Simple stateless backends with very uniform request cost. | Can create hot-spots due to statistical variance, especially under low traffic. |

### The SDE-2 Must-Know: Power of Two Choices
Instead of random selection, pick 2 servers at random and route to the **less loaded** of the two. Statistically achieves near-optimal distribution without the overhead of tracking all servers:
*   **$O(1)$ decision** instead of $O(N)$ scan.
*   Used by NGINX, HAProxy, and Envoy in production.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Request distribution variance across backends`: High variance = poor algorithm fit.
    *   `Backend connection queue depth`: Long queues on specific backends indicate hot-spot routing.
*   **Blast Radius (The "Impact"):**
    *   Round Robin sending requests to a crashed-but-not-yet-detected server causes request failures until the passive health check marks it down.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Defaulting to Round Robin for all workloads without considering whether requests have uniform duration (Round Robin falls apart for long-running requests like file uploads).
*   Not knowing Least Connections requires the LB to maintain connection state.

### Interview Tip (The "Strong Hire" Signal)
> *"For our WebSocket-heavy chat service, Round Robin would distribute connections evenly on arrival but ignore that some servers accumulate far more open sockets over time. We use **Least Connections** to route new WebSocket upgrades to the server currently bearing the fewest open connections."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

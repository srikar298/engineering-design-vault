# ⚡ 08 - WebSockets

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C013 |
| **Category** | Networking |
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
*   **Two-Sentence Trigger:** WebSockets provide a **full-duplex**, persistent TCP connection over a single HTTP upgrade — once established, both the client and server can send messages to each other at any time without the overhead of repeated HTTP request/response cycles. This makes WebSockets the right choice for true real-time, bidirectional communication: live chat, collaborative editors, multiplayer games, live dashboards, and trading platforms.
*   **Scalability Dimension:** Primary: **Real-Time Bidirectional Communication**. Secondary: **Connection State Management at Scale**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### The WebSocket Upgrade Handshake
```
Client ──► GET /chat HTTP/1.1
           Upgrade: websocket
           Connection: Upgrade
           Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==

Server ◄── HTTP/1.1 101 Switching Protocols
           Upgrade: websocket
           Connection: Upgrade
           Sec-WebSocket-Accept: s3pPLMBiTxaQ9kYGzzhZRbK+xOo=

           [TCP connection is now a persistent WebSocket connection]
           [Both sides can send frames at any time] ✅
```

### WebSocket vs Polling vs SSE
| Feature | Short Polling | Long Polling | SSE | WebSocket |
| :--- | :--- | :--- | :--- | :--- |
| **Direction** | Client-only pull. | Client-only pull. | Server-only push. | Full-duplex (both). |
| **Latency** | Defined by poll interval (high). | Low (responds immediately when data available). | Low (server pushes immediately). | Lowest (no request overhead). |
| **Connection** | New HTTP request per poll. | HTTP request held open until data or timeout. | Persistent SSE connection (HTTP). | Persistent TCP connection (WS). |
| **Overhead** | High — repeated HTTP headers. | Moderate — one connection, wait. | Low — HTTP/2 multiplexed. | Lowest — binary WS frames. |
| **Browser Support** | ✅ Universal. | ✅ Universal. | ✅ Universal (not IE11). | ✅ Universal (IE10+). |
| **Auto-Reconnect** | Client retries naturally. | Client retries naturally. | ✅ Built-in EventSource API. | ❌ Must implement manually. |
| **Use Case** | Activity feeds, notifications (acceptable latency). | Notifications, older browsers. | Live dashboards, notifications (server → client only). | Chat, games, collaborative editing. |

### WebSocket at Scale — The Connection Problem
A WebSocket is a **stateful**, long-lived TCP connection held to a **specific server instance**. This breaks horizontal scaling:
*   Client A's connection is on Server 1.
*   Client B's connection is on Server 2.
*   Client A sends a message to Client B → it arrives at Server 1, but Client B's socket is on Server 2.

**Solution: Pub/Sub Backplane (Redis Pub/Sub / Kafka)**
```
Client A ──► Server 1 ──► Redis Pub/Sub ──► Server 2 ──► Client B
                          (broadcasts message to all WS server instances)
```
Each WebSocket server subscribes to a Redis channel. When any server receives a message, it publishes to Redis, and Redis fans it out to all other WS server instances.

### Numbers to Know
*   A single WebSocket server (Node.js) can hold ~100k–1M concurrent connections depending on RAM.
*   Each WebSocket connection consumes ~2–10KB of RAM on the server.
*   1M connections at 5KB each = ~5GB RAM.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Active WebSocket Connection Count`: Primary scaling metric. Triggers horizontal scaling.
    *   `WebSocket Disconnection Rate`: Spikes indicate network instability or server crashes (clients losing connections).
*   **Blast Radius (The "Impact"):**
    *   A WebSocket server restart **drops all active connections** — clients must reconnect. With no reconnection logic, users lose their session entirely. Implement **exponential backoff + session resume** on the client.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Not addressing the **sticky session or pub/sub backplane** problem when multiple WebSocket servers are used behind a load balancer (load balancer must route the same client to the same server, or use a Redis backplane for cross-server messaging).
*   Using WebSockets when SSE is sufficient (if the server only needs to push data to clients, SSE is simpler and auto-reconnects).

### Interview Tip (The "Strong Hire" Signal)
> *"Our chat system uses WebSockets. Each connection is sticky-routed to a specific WebSocket server via a consistent hash on user_id at the load balancer. For cross-user messaging, we use a Redis Pub/Sub backplane: when Server 1 receives a message for User B, it publishes to Redis channel `user:B`. Server 2, holding User B's socket, is subscribed and delivers the frame immediately."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

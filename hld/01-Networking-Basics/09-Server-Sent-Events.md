# ⚡ 09 - Server-Sent Events (SSE)

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C012 |
| **Category** | Networking |
| **Difficulty** | 🟢 Easy |
| **Interview Frequency** | 🟡 Medium |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Server-Sent Events (SSE) is a unidirectional HTTP-based protocol where the server holds open a persistent HTTP connection and continuously streams events to the client using a standard `text/event-stream` content type — the client cannot send data back over the same connection. SSE is the right choice when you only need **server-to-client streaming** and want the simplicity of plain HTTP with built-in browser reconnection support, without the overhead and statefulness of WebSockets.
*   **Scalability Dimension:** Primary: **Server-to-Client Real-Time Push** with minimal operational complexity.

---

## ⚖️ 2. Trade-offs & Deep Dive

### SSE Wire Format
```http
GET /api/events HTTP/1.1
Accept: text/event-stream

HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache
Connection: keep-alive

data: {"type":"order_update","order_id":"123","status":"shipped"}\n\n

id: 42
data: {"type":"notification","message":"Your item has shipped!"}\n\n

event: custom-event-name
data: {"payload":"value"}\n\n

: this is a comment, ignored by browser\n\n
```

*   `data:` — The event payload.
*   `id:` — Event ID. Browser sends `Last-Event-ID` header on reconnect → resume from last seen event.
*   `event:` — Custom event name (browser `EventSource` fires it as a named event).
*   `retry:` — Milliseconds before browser auto-reconnects after disconnect.

### SSE vs WebSocket Decision Matrix
| Dimension | SSE | WebSocket |
| :--- | :--- | :--- |
| **Communication** | Server → Client only. | Full-duplex (both). |
| **Protocol** | HTTP (works through any HTTP/1.1 or HTTP/2 proxy). | WS (requires proxy/LB WebSocket support). |
| **Auto-Reconnect** | ✅ Built into `EventSource` browser API with `Last-Event-ID`. | ❌ Must implement manually. |
| **HTTP/2 Multiplexing** | ✅ Multiple SSE streams share one HTTP/2 connection. | ❌ Each WS connection is independent. |
| **Max Browser Connections** | HTTP/2: unlimited (multiplexed). HTTP/1.1: 6 per origin. | No browser limit. |
| **Firewall/Proxy Friendly** | ✅ Standard HTTP port 80/443, no special config. | ⚠️ Some enterprise proxies block WS upgrades. |
| **Binary Data** | ❌ Text only (Base64 encode binary). | ✅ Native binary frames. |
| **Complexity** | Low — standard HTTP endpoint. | Higher — stateful connection management. |

### The Auto-Reconnect + Last-Event-ID Pattern
```
Client connects → Server streams events with IDs:
id: 100, data: ...
id: 101, data: ...

Server crashes → Browser EventSource auto-reconnects:
GET /events
Last-Event-ID: 101

Server resumes from event 102 → zero message loss ✅
(Server must store recent events in Redis for replay on reconnect)
```

### Real-World SSE Use Cases
*   **ChatGPT response streaming:** SSE streams LLM tokens progressively to the browser.
*   **CI/CD live build logs:** Stream build output to the browser as it happens.
*   **Live order tracking:** Push order status updates.
*   **Stock price tickers, sports scores, leaderboards.**

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Active SSE Connection Count`: Long-lived connections — monitor for connection leaks.
    *   `SSE Reconnect Rate`: High rate indicates frequent server-side disconnections or timeouts.
*   **Blast Radius (The "Impact"):**
    *   Under HTTP/1.1, browsers limit **6 connections per origin** — 6 SSE streams would consume all connections on a domain. HTTP/2 resolves this (streams are multiplexed). Always serve SSE over HTTP/2.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Reaching for WebSockets when the use case is purely server-push (ChatGPT response streaming, notifications, live dashboards) — SSE is simpler, auto-reconnects, and works through corporate proxies.
*   Not mentioning `Last-Event-ID` for fault tolerance — without it, SSE reconnects miss events published during the disconnect window.

### Interview Tip (The "Strong Hire" Signal)
> *"For our live order tracking dashboard, we use SSE instead of WebSockets. It's unidirectional (server → client only), auto-reconnects via `EventSource`, and works through any HTTP proxy without special firewall rules. We assign each event an ID stored in Redis. On reconnect, the client sends `Last-Event-ID`, and we replay missed events from Redis — zero message loss."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

# 🌐 01 - Networking Basics (C004-C014)

## 🧭 Networking Study Path
Use this structured path aligned with your **Google Sheet Tracker** to master network layers and API protocols:

### 🟢 1. Transport & Directory Services
*   [C004 - TCP vs UDP](./01-TCP-vs-UDP.md)
*   [C006 - TLS/SSL](./02-TLS-SSL.md)
*   [C005 - DNS Resolution](./03-DNS-Resolution.md)

### 🟡 2. Web Protocols & API Paradigms
*   [C007 - HTTP/1.1 vs HTTP/2 vs HTTP/3](./04-HTTP-Versions.md)
*   [C010 - gRPC](./05-gRPC.md)
*   [C009 - REST vs GraphQL](./06-REST-vs-GraphQL.md)
*   [C008 - Content Negotiation](./07-Content-Negotiation.md)

### 🔴 3. Real-Time Communication
*   [C013 - WebSockets](./08-WebSockets.md)
*   [C012 - Server-Sent Events (SSE)](./09-Server-Sent-Events.md)
*   [C011 - Long Polling](./10-Long-Polling.md)

---

## 📖 The Concept
Before a request ever hits your Load Balancer, it must traverse the public internet. Understanding this journey is essential for debugging high-level latency issues.

## 📊 The SDE-2 Trade-off Table: Protocols

| Protocol | Characteristic | Use Case |
| :--- | :--- | :--- |
| **TCP** | Reliable, Ordered, Error-checked. High overhead (3-way handshake). | Web pages, APIs, Database connections. |
| **UDP** | Fire-and-forget. Fast, no guarantees. | Video streaming, Gaming, VoIP. |
| **HTTP/2** | Multiplexing over a single TCP connection. | Modern web applications (reduces latency). |
| **HTTP/3** | Built on QUIC (UDP). No Head-of-Line blocking. | Next-gen web, High-packet-loss environments (Mobile). |

## 🚫 The Interview Trap
**"I will use a CDN to cache API responses."**
CDNs are designed for static assets (Images, CSS, JS), not dynamic API responses. 
*Better Answer:* "I'll use a CDN to serve static frontend assets globally. For API responses, I'll use Redis internally or an API Gateway with edge-caching enabled if the data is highly cacheable."

## 🚀 The SDE-3 Edge: Global Traffic Steering

### 1. DNS Resolution Architecture
When asked what happens when you type `google.com`, don't just say "It gets the IP." Mention the recursive steps:
1. Browser Cache -> OS Cache -> Router Cache -> ISP Resolver.
2. If missed: Root Server -> TLD Server (.com) -> Authoritative Name Server (Route53).
3. Mention **DNS Routing Policies**: Latency-based routing, Geo-DNS, or Weighted routing for Multi-Region deployments.

### 2. Anycast Routing
How does a CDN provide one IP that routes to 100+ different countries?
**The SDE-3 Answer:** "We use **IP Anycast**. The same IP address is announced from multiple locations using BGP (Border Gateway Protocol). The internet's routing infrastructure then automatically routes the user to the 'closest' PoP (Point of Presence) based on network hop count."

### 3. CDN Invalidation Strategies
Don't just mention CDNs; talk about how you manage them.
*   **Purge (Hard Delete):** Telling the CDN to delete the object from its cache. Fast but expensive (causes a spike in origin traffic).
*   **Versioning (File Hashing):** Changing the filename (e.g., `main.abcd123.js`). This is the **Gold Standard** because it's instant and safe.
*   **TTL (Time-to-Live):** Letting the object expire naturally. Use for non-critical assets.

**Senior Signal:** "For our high-traffic frontend, we use **immutable file hashing** and a long TTL (1 year). This guarantees that users always get the latest code without us ever having to trigger costly manual CDN purges."

---

# 🌐 01 - Networking Basics (SDE-2 Refresher)

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

## 🚀 The SDE-3 Edge: DNS Resolution Architecture
When asked what happens when you type `google.com`, don't just say "It gets the IP." Mention the recursive steps:
1. Browser Cache -> OS Cache -> Router Cache -> ISP Resolver.
2. If missed: Root Server -> TLD Server (.com) -> Authoritative Name Server (Route53).
3. Mention **DNS Routing Policies**: Latency-based routing, Geo-DNS, or Weighted routing for Multi-Region deployments.

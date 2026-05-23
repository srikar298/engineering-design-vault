# 🔌 19 - API Design (C015, C016)

## 📖 1. The Concept
Communication between services is the backbone of HLD. While REST is the industry default, gRPC is the "High-Performance" choice for internal microservices.

---

## 📊 2. The SDE-2 Trade-off Table

| Feature | REST (JSON) | gRPC (Protobuf) |
| :--- | :--- | :--- |
| **Protocol** | HTTP/1.1 (usually) | HTTP/2 (Multiplexing) |
| **Payload** | Text-based (Large) | Binary-based (Small/Compact) |
| **Strictness** | Loosely coupled | Strict (Schema-defined `.proto`) |
| **Streaming** | Request-Response only | Bi-directional Streaming |
| **Browser Support** | Native (Best for Frontend) | Limited (Needs gRPC-web) |

---

## 🏗️ 3. When to use which?
- **Use REST for:** Public-facing APIs, where ease of consumption and browser compatibility are critical.
- **Use gRPC for:** Internal service-to-service communication in a high-throughput system (10k+ concurrent users) to reduce serialization overhead and latency.

---

## 🚀 4. The SDE-3 Edge: Idempotency in API Design
If an API call fails due to a network timeout, should the client retry? 
- **GET/PUT/DELETE**: Usually idempotent by nature.
- **POST**: NOT idempotent. 
- **The Solution:** Always include an `X-Idempotency-Key` in the header. The server checks Redis for this key before processing. If it exists, return the cached successful response without re-processing.

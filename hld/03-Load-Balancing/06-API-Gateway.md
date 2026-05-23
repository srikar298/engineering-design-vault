# ⚡ 06 - API Gateway

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C048 |
| **Category** | Load Balancing / Edge |
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
*   **Two-Sentence Trigger:** An API Gateway is the single entry point for all client API requests in a microservices architecture, acting as an orchestration layer that handles cross-cutting concerns before routing traffic to the correct downstream service. It centralizes authentication, rate limiting, request routing, protocol translation, and observability — keeping these concerns out of individual service code.
*   **Scalability Dimension:** Primary: **Cross-Cutting Concern Centralization** & **Client Experience Simplification**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### API Gateway vs Reverse Proxy vs Load Balancer
| Concept | Primary Role | Operates At |
| :--- | :--- | :--- |
| **Load Balancer** | Distributes traffic across homogeneous servers. | L4 or L7 — IP/HTTP level. |
| **Reverse Proxy** | Hides backends, handles TLS/caching at edge. | L7 — HTTP level. |
| **API Gateway** | Application-aware routing, auth, rate limiting, transformation, composition. | L7 — Business logic level. |

### What an API Gateway Does
1. **Authentication & Authorization:** Validates JWTs, API Keys, or OAuth tokens. Rejects unauthenticated requests at the edge — microservices never receive unauthenticated traffic.
2. **Rate Limiting:** Enforces per-client, per-endpoint request quotas (e.g., 1000 req/min per API key).
3. **Request Routing:** Routes `/api/users/*` to the User Service, `/api/orders/*` to the Order Service.
4. **Protocol Translation:** Translates REST (HTTP/JSON) to gRPC for backend microservices, or WebSocket to HTTP for backend compatibility.
5. **Request/Response Transformation:** Adds headers, strips internal fields from responses, renames fields for backward compatibility.
6. **API Composition (BFF):** Fans out a single client request to multiple microservices, aggregates responses, and returns a unified payload (**Backend for Frontend** pattern).
7. **Observability:** Centralized request logging, distributed trace ID injection, and metrics collection.

### Examples
| Tool | Type | Best For |
| :--- | :--- | :--- |
| **AWS API Gateway** | Managed Cloud | Serverless + AWS-native integrations. |
| **Kong** | Open-source + plugins | Self-hosted, plugin ecosystem. |
| **Nginx + Lua** | Custom | High performance, custom logic. |
| **Envoy + Istio** | Service Mesh Gateway | mTLS, observability, microservice-native. |

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Gateway P99 Latency`: Measures auth + routing overhead added by the gateway layer.
    *   `Auth Rejection Rate`: Sudden spikes indicate credential stuffing or misconfigured clients.
*   **Blast Radius (The "Impact"):**
    *   **The Gateway is a SPOF.** If the API Gateway cluster crashes, 100% of external client traffic is blocked. Must be deployed in HA clusters across multiple AZs.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Putting **business logic** in the API Gateway (e.g., calculating pricing or transforming business entities) — the gateway should only handle infrastructure-level cross-cutting concerns.
*   Not accounting for the gateway as a bottleneck — if 100k RPS flows through it, every millisecond of latency added per request matters.

### Interview Tip (The "Strong Hire" Signal)
> *"In our microservices architecture, all external traffic flows through Kong API Gateway. It handles JWT validation, rate limiting per API key, and path-based routing to 20+ downstream services. This keeps auth code out of each microservice and gives us a single place to add new cross-cutting policies without redeploying services."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

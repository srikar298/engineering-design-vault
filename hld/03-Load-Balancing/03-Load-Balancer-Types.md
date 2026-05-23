# ⚡ 03 - Load Balancer Types

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C043 |
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
*   **Two-Sentence Trigger:** Load Balancers operate at different layers of the network stack, with Layer 4 (Transport) LBs routing based on IP/TCP headers blindly and Layer 7 (Application) LBs routing based on full HTTP payload content. The choice between them determines speed, flexibility, SSL termination capability, and cost.
*   **Scalability Dimension:** Primary: **Request Routing Flexibility** vs. **Throughput Speed**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Layer 4 vs Layer 7
| Feature | Layer 4 (Transport LB) | Layer 7 (Application LB) |
| :--- | :--- | :--- |
| **OSI Layer** | Layer 4 — TCP/UDP | Layer 7 — HTTP/HTTPS/gRPC |
| **What It Sees** | IP address, source/destination port only. Cannot read payload. | Full HTTP headers, URL paths, cookies, body (after TLS termination). |
| **Routing Logic** | Blind forwarding — no content awareness. | Path-based, header-based, host-based routing. |
| **TLS/SSL** | Pass-through (no termination) or terminate at LB. | Terminates TLS, inspects decrypted payload, re-encrypts to backend. |
| **Speed** | Extremely fast — sub-millisecond, minimal processing. | Slower — decryption, parsing, re-encryption overhead. |
| **Use Cases** | TCP game servers, raw database proxies, SMTP, high-frequency trading. | REST APIs, microservice routing, A/B testing, canary deployments. |
| **Examples** | AWS NLB, HAProxy (TCP mode), IPVS. | AWS ALB, NGINX (HTTP mode), Envoy, Traefik. |

### Hardware vs Software LBs
| Type | Examples | Trade-off |
| :--- | :--- | :--- |
| **Hardware LB** | F5 BIG-IP, Citrix ADC | Maximum throughput (10M+ RPS). Vendor lock-in. Expensive ($100k+). |
| **Software LB** | NGINX, HAProxy, Envoy | Flexible, cheap, cloud-native. Limited by CPU. |
| **Cloud Managed** | AWS ALB/NLB, GCP Cloud LB | Zero ops overhead. Autoscales automatically. Higher per-request cost. |
| **Client-Side LB** | Ribbon (Netflix), gRPC built-in | No LB hop — clients hold server registry and route directly. Operational complexity in keeping registry fresh. |

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `LB CPU utilization`: L7 LBs doing TLS termination are CPU-bound. Spike = need horizontal LB scaling.
    *   `SSL Handshake Latency`: High values indicate certificate processing bottlenecks.
*   **Blast Radius (The "Impact"):**
    *   The Load Balancer itself is a potential SPOF. Production deployments always run LBs in **Active-Active** or **Active-Passive** pairs with shared VIPs (Virtual IPs) for failover.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Not knowing that L7 LBs must terminate TLS (they decrypt traffic to read HTTP headers, making end-to-end encryption a separate concern — mTLS to backend required).
*   Proposing L7 LB for a raw TCP gaming server (unnecessary parsing overhead).

### Interview Tip (The "Strong Hire" Signal)
> *"We use an AWS NLB (Layer 4) in front of our WebSocket servers for ultra-low latency connection setup, then an ALB (Layer 7) in front of our HTTP API fleet for path-based routing to microservices. The NLB passes TLS through to our servers which terminate it themselves, preserving client IP for geo-routing logic."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

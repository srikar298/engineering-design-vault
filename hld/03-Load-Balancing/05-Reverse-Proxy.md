# ⚡ 05 - Reverse Proxy

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C047 |
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
*   **Two-Sentence Trigger:** A Reverse Proxy is a server that sits in front of backend servers, intercepting all inbound client requests and forwarding them to the appropriate backend. Unlike a forward proxy (which serves clients), a reverse proxy serves backends — hiding their topology, terminating TLS, caching responses, and compressing payloads.
*   **Scalability Dimension:** Primary: **Backend Protection / Abstraction** & **Edge Performance (caching, compression)**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Reverse Proxy vs Forward Proxy vs Load Balancer
| Concept | Direction | Primary Purpose |
| :--- | :--- | :--- |
| **Forward Proxy** | Client → Proxy → Internet | Hides the client. Used for content filtering, anonymization, corporate egress. |
| **Reverse Proxy** | Internet → Proxy → Backend Servers | Hides the backends. Handles TLS, caching, compression, routing. |
| **Load Balancer** | Internet → LB → Multiple Backends | Distributes load. Often combined with Reverse Proxy in practice (NGINX does both). |

### What a Reverse Proxy Does
1. **TLS Termination:** Handles the expensive TLS handshake at the edge; communicates with backends over plain HTTP internally (or re-encrypted mTLS).
2. **Static Content Caching:** Caches responses (e.g., `Cache-Control: max-age=3600`) to serve repeat requests without hitting the backend.
3. **Compression:** Gzip/Brotli compresses responses before sending to clients, reducing bandwidth.
4. **Request Buffering:** Absorbs slow client upload connections (e.g., slow mobile uploads) so backends receive data at full speed.
5. **DDoS Mitigation:** Drops malformed packets, rate-limits IPs, and absorbs connection floods before they reach application servers.
6. **Backend Anonymization:** Clients never see backend IPs, hostnames, or server software headers.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Proxy Cache Hit Ratio`: Low ratios waste backend compute cycles on cacheable responses.
    *   `TLS Handshake Duration`: Spikes indicate certificate issues or CPU saturation on the proxy tier.
*   **Blast Radius (The "Impact"):**
    *   If the reverse proxy tier crashes, the **entire application is unreachable** to clients — even if all backends are perfectly healthy. Reverse proxies must run in HA pairs.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Conflating a Reverse Proxy with a Load Balancer (a Load Balancer's primary job is traffic distribution; a Reverse Proxy's primary job is backend abstraction and edge processing — NGINX does both, but they are distinct concepts).
*   Not knowing that TLS termination at the reverse proxy means backend-to-proxy traffic is unencrypted unless you explicitly configure mTLS.

### Interview Tip (The "Strong Hire" Signal)
> *"We run NGINX as a reverse proxy at the edge. It terminates TLS, caches our static API responses at the edge (saving ~40% of backend compute), and buffers slow client connections to protect our application servers from slow-loris attacks. Backend communication is secured via mTLS."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

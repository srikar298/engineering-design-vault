# ⚡ 07 - TLS/SSL Handshake

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C136 |
| **Category** | Security Basics |
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
*   **Two-Sentence Trigger:** The TLS/SSL Handshake is the initial cryptographic negotiation protocol that establishes a secure, encrypted socket connection between a client and a server. It is triggered when a client initiates a request to an HTTPS resource (`https://` over TCP port 443), negotiating cipher suites, authenticating the server's certificate, and generating shared symmetric session keys to encrypt subsequent HTTP payloads.
*   **Scalability Dimension:** Primary: **Handshake Round-Trip Latency (RTT) vs. CPU Encryption Computation Limits**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### TLS 1.2 (2-RTT) vs. TLS 1.3 (1-RTT) Handshake
Modern systems enforce TLS 1.3 to remove a full network round-trip of latency:
```
TLS 1.2 Handshake (2 Round-Trips):
  Client                                                      Server
    │── 1. ClientHello (Supported ciphers, random) ──────────► │
    │◄── 2. ServerHello + Certificate + ECDH Key Share ────────│
    │── 3. Client Key verification & Finished ────────────────► │
    │◄── 4. ChangeCipherSpec & Finished ───────────────────────│  (Secure session starts)

TLS 1.3 Handshake (1 Round-Trip):
  Client                                                      Server
    │── 1. ClientHello + ECDH Key Share Guess ────────────────► │
    │◄── 2. ServerHello + Key verification + Finished ─────────│  (Secure session starts)
```

### The Three Pillars of the Handshake
1. **Negotiation (Cipher Suite Matching):**
   * Client and Server agree on the TLS version and cipher suite (e.g., `TLS_AES_256_GCM_SHA384` for symmetric encryption, key exchange, and hashing).
2. **Authentication (Certificate Verification):**
   * The server presents its SSL Certificate. The client verifies the certificate signature against its pre-installed list of trusted root Certificate Authorities (CAs). If verification fails, the browser displays a security warning and closes the socket.
3. **Key Exchange (The Diffie-Hellman Magic):**
   * Rather than sending keys over the wire, client and server use **ECDH (Elliptic Curve Diffie-Hellman)**. Both exchange public mathematical vectors. Using their private keys and the other's public vector, both calculate the exact same **Pre-Master Secret** locally, deriving the **Symmetric Session Key**.

---

### SDE-3 Optimization: 0-RTT Resumption & Replay Attacks
In TLS 1.3, if a client has connected before, they can use **0-RTT (Zero Round-Trip Time) Resumption** to send encrypted application data in the very first packet (`ClientHello`).
*   *The Security Leak:* Vulnerable to **Replay Attacks**. An eavesdropping attacker can intercept the client's 0-RTT packet and replay (re-transmit) it to the server. If the packet is a state-changing operation (e.g., `POST /transfer-money`), the server could execute the request twice.
*   *The Mitigation:* Always configure CDNs, Load Balancers, and API Gateways to **reject 0-RTT data for state-changing HTTP requests** (POST, PUT, DELETE), only permitting it for safe read queries (GET).

---

## 💥 3. Resiliency & Operations

### SSL Termination Architecture
Executing RSA/ECDH mathematics for thousands of concurrent TLS handshakes consumes massive CPU cycles.
*   **The Trap:** Terminating TLS directly on your web application servers. This drains CPU availability away from your business logic, causing application thread bottlenecks.
*   **The Gold Standard:** Enforce **SSL Termination (Offloading)** at your Edge CDN (Cloudflare) or Application Load Balancer (ALB). The load balancer handles the heavy handshake math and routes plain HTTP to your internal app servers over a secured private VPC network.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Believing that asymmetric public/private keys are used to encrypt the entire HTTPS payload transfer. Asymmetric math is too slow. Asymmetric encryption is *only* used to exchange the keys; **symmetric encryption (AES-256) is used for the actual payload transfer**.
*   Not knowing the difference in latency round-trips between TLS 1.2 (2-RTT) and TLS 1.3 (1-RTT).

### Interview Tip (The "Strong Hire" Signal)
> *"To optimize our API connection speeds, we enforce TLS 1.3 to establish secure connections in 1-RTT. To prevent CPU starvation on our core application servers under load, we offload SSL termination to our Edge CDN and Application Load Balancers. We support TLS 1.3 0-RTT session resumption for GET requests to achieve instant loads, but explicitly reject 0-RTT on all POST and PUT API routes to completely neutralize replay attacks."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

# ⚡ 02 - TLS/SSL

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C006 |
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
*   **Two-Sentence Trigger:** TLS (Transport Layer Security) is a cryptographic protocol that provides **confidentiality** (encryption — no eavesdropping), **integrity** (MAC — data not tampered), and **authentication** (certificates — you're talking to who you think) over an otherwise untrusted TCP connection. SSL is the deprecated predecessor to TLS; the terms are used interchangeably colloquially but all modern systems use TLS 1.2 or 1.3.
*   **Scalability Dimension:** Primary: **Security** (Confidentiality + Integrity + Authentication). Secondary: **Latency cost** of the TLS handshake.

---

## ⚖️ 2. Trade-offs & Deep Dive

### TLS 1.2 Handshake (Full)
```
Client                          Server
  │── ClientHello ─────────────► │  (Supported cipher suites, TLS version, random)
  │◄── ServerHello + Certificate │  (Chosen cipher, server's public key cert)
  │◄── ServerHelloDone ──────────│
  │── ClientKeyExchange ────────► │  (Pre-master secret, encrypted with server public key)
  │── ChangeCipherSpec ─────────► │
  │── Finished ────────────────► │
  │◄── ChangeCipherSpec ─────────│
  │◄── Finished ─────────────────│
  │                               │
  2 round-trips before first byte of application data
```

### TLS 1.3 Handshake (Improved)
```
Client                          Server
  │── ClientHello + Key Share ──► │  (Key share eliminates separate round trip)
  │◄── ServerHello + Cert + Fin ──│
  │── Finished ────────────────► │
  │── [APPLICATION DATA] ───────► │
  1 round-trip to first byte. 0-RTT resumption possible for repeat connections.
```

### TLS 1.2 vs TLS 1.3
| Feature | TLS 1.2 | TLS 1.3 |
| :--- | :--- | :--- |
| **Handshake Round-Trips** | 2 | 1 |
| **0-RTT Resumption** | ❌ No | ✅ Yes (session ticket replay — replay attack risk) |
| **Forward Secrecy** | Optional (not always enforced). | ✅ Mandatory (ephemeral Diffie-Hellman always). |
| **Cipher Suites** | Many (including weak ones like RC4). | Pruned to 5 secure suites only. |

### mTLS (Mutual TLS)
In standard TLS, only the **server** presents a certificate (client verifies server identity).
In **mTLS**, **both** parties present certificates — the server also verifies the client's identity.
*   **Use Case:** Service-to-service authentication inside a cluster (Service Mesh / Istio), API client authentication, Zero-Trust networks.
*   **Trade-off:** Certificate management complexity per client. Requires a PKI (Private Certificate Authority) per organization.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `TLS Handshake Duration (ms)`: Spikes indicate CPU saturation on TLS termination layer.
    *   `Certificate Expiry Days`: Expired cert = 100% of users see a browser warning. Auto-renew via Let's Encrypt / ACM.
*   **Blast Radius (The "Impact"):**
    *   Expired TLS certificate = **complete service outage** for browser clients. Automated cert rotation is non-negotiable.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Saying "SSL" when you mean TLS (shows you're not up to date — SSL was deprecated in 2015).
*   Not knowing the difference between mTLS and standard TLS — interviewers probe this specifically for service mesh discussions.
*   Not knowing that **Forward Secrecy** means that even if the server's private key is compromised later, past recorded sessions cannot be decrypted (because session keys were ephemeral and not stored).

### Interview Tip (The "Strong Hire" Signal)
> *"Inside our Kubernetes cluster, Istio enforces mTLS between all services. Each pod sidecar presents a SPIFFE/SPIRE-issued certificate scoped to its service identity. This gives us Zero-Trust — no service can impersonate another, even within the cluster perimeter."*

---

## 💡 5. My Custom Study Notes & Whiteboard
```
Key TLS Concepts Summary:
- Confidentiality → Symmetric encryption (AES-256) using session key
- Integrity       → HMAC (hash of message + secret key)
- Authentication  → Asymmetric PKI (RSA/ECDSA certificate chain → CA root)
- Forward Secrecy → Ephemeral DH key exchange (session key never persisted)

TLS 1.3 = 1-RTT handshake + mandatory forward secrecy + 0-RTT session resumption
mTLS = both sides present certs → client + server authenticated
```

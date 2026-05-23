# ⚡ 04 - HTTP/1.1 vs HTTP/2 vs HTTP/3

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C007 |
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
*   **Two-Sentence Trigger:** HTTP/1.1 sends one request per TCP connection at a time, causing Head-of-Line Blocking; HTTP/2 solves this with multiplexing (many streams over one TCP connection) and header compression; HTTP/3 eliminates TCP-level Head-of-Line Blocking entirely by building on QUIC (a UDP-based reliable transport). Each version's performance improvement targets a different bottleneck in the web stack.
*   **Scalability Dimension:** Primary: **Request Latency** & **Connection Efficiency**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### The Core Problem Each Version Solves

**HTTP/1.1 Problem — Connection Overhead & HOLB:**
```
Connection 1: GET /html ───► [wait...] ───► GET /css ───► [wait...] ───► GET /js
              (Sequential — each request waits for the previous to complete)

Workaround: Open 6 parallel TCP connections per origin (browser limit)
            But 6 TCP handshakes + 6 TLS handshakes = massive overhead
```

**HTTP/2 Solution — Multiplexing:**
```
Single TCP Connection:
  Stream 1: GET /html  ──────────────────────────────────────► response
  Stream 2: GET /css   ─────────────────────────────────────► response
  Stream 3: GET /js    ────────────────────────────────────► response
  Stream 4: GET /image ───────────────────────────────────► response
  (All concurrent, no waiting — one TLS handshake, one TCP connection)
```

**HTTP/3 Problem with HTTP/2 — TCP-level HOLB:**
```
HTTP/2 over TCP: One packet dropped in the TCP stream
→ ALL streams freeze waiting for that packet's retransmission
→ TCP-level Head-of-Line Blocking even though HTTP/2 has multiple streams
```

**HTTP/3 Solution — QUIC (UDP-based):**
```
HTTP/3 over QUIC:
  Stream 1: GET /html  ──────────────────► (independent QUIC stream)
  Stream 2: GET /css   ──────────────────► (independent QUIC stream)
  Stream 3: GET /js    ──────────────────► (PACKET DROPPED HERE)
  Stream 4: GET /image ──────────────────► (independent — NOT blocked!)
  Stream 3 retransmits independently without blocking other streams ✅
```

### Full Comparison Table
| Feature | HTTP/1.1 | HTTP/2 | HTTP/3 |
| :--- | :--- | :--- | :--- |
| **Transport** | TCP | TCP | QUIC (UDP) |
| **TLS** | Optional (but HTTPS required in practice) | Mandatory (always encrypted). | Mandatory (built into QUIC). |
| **Multiplexing** | ❌ One request per connection (pipelining rarely works). | ✅ Multiple streams over one connection. | ✅ Multiple independent QUIC streams. |
| **Header Compression** | ❌ Headers repeated verbatim on every request. | ✅ HPACK (Huffman encoded, stateful dictionary). | ✅ QPACK (HPACK adapted for QUIC). |
| **HOLB at HTTP layer** | ✅ Always blocked. | ❌ Solved. | ❌ Solved. |
| **HOLB at Transport layer** | ✅ Always (TCP). | ✅ Still blocked (TCP). | ❌ Solved (QUIC independent streams). |
| **Connection Setup** | TCP + TLS 1.2 = 2+ RTT. | TCP + TLS = 2+ RTT. | QUIC = 1 RTT (0-RTT on resume). |
| **Connection Migration** | ❌ IP change drops connection. | ❌ IP change drops connection. | ✅ QUIC Connection ID survives IP change (mobile handoff). |

### Numbers to Know
*   HTTP/2 adoption: ~65% of websites (2024).
*   HTTP/3 adoption: ~30% of websites (2024), ~95% of requests at Cloudflare.
*   QUIC 0-RTT resumption saves an entire round-trip for returning users.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Protocol version distribution in access logs`: Tells you which clients aren't upgrading.
    *   `TTFB (Time To First Byte)`: HTTP/2 and HTTP/3 should show significantly lower TTFB vs HTTP/1.1 for multi-resource pages.
*   **Blast Radius (The "Impact"):**
    *   HTTP/2 server push (now deprecated in most browsers) could cause bandwidth waste if misconfigured.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Thinking HTTP/2 multiplexing fully solves HOLB — it solves it at the HTTP layer but **TCP-level HOLB remains** (a single dropped TCP packet stalls all HTTP/2 streams on that connection).
*   Not knowing HTTP/3 runs on **UDP** (via QUIC) — this is the foundational architectural difference.

### Interview Tip (The "Strong Hire" Signal)
> *"We serve our frontend via Cloudflare, which negotiates HTTP/3 with supporting browsers. This gives us 0-RTT connection resumption for repeat visitors and QUIC stream independence — a single dropped UDP packet only stalls the affected resource, not the whole page load. On mobile networks with frequent IP changes, QUIC's Connection ID means seamless handoffs from WiFi to cellular."*

---

## 💡 5. My Custom Study Notes & Whiteboard
```
The HOLB Journey:
HTTP/1.1: HOLB at application layer (requests sequential per connection)
HTTP/2:   Solved at application layer. HOLB still exists at TCP layer.
HTTP/3:   Solved at BOTH layers. QUIC streams are independent at transport.

Connection Efficiency:
HTTP/1.1 for 10 resources: 10 TCP handshakes (or 6 parallel connections + pipeline)
HTTP/2  for 10 resources: 1 TCP handshake + 10 concurrent streams
HTTP/3  for 10 resources: 1 QUIC handshake (0-RTT on resume) + 10 independent streams
```

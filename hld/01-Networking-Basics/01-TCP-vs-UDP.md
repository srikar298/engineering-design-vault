# ⚡ 01 - TCP vs UDP

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C004 |
| **Category** | Networking |
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
*   **Two-Sentence Trigger:** TCP (Transmission Control Protocol) provides reliable, ordered, and error-checked delivery of bytes over a network, using a 3-way handshake to establish connections and ACK/retransmit for every packet — guaranteeing delivery at the cost of latency. UDP (User Datagram Protocol) sends packets with no connection setup, no ordering, and no retransmission — trading reliability for the lowest possible latency and overhead.
*   **Scalability Dimension:** Primary: **Reliability vs Latency Trade-off** per use case.

---

## ⚖️ 2. Trade-offs & Deep Dive

### The TCP 3-Way Handshake
```
Client          Server
  │── SYN ──────────► │   (Client: "I want to connect, seq=100")
  │◄── SYN-ACK ──────│   (Server: "OK, seq=200, ack=101")
  │── ACK ──────────► │   (Client: "Confirmed, ack=201")
  │                   │
  │── [DATA] ───────► │   Connection established — data can flow
```
*   **Cost:** 1.5 round-trips before the first byte of application data.
*   **On modern internet (RTT=50ms):** This handshake adds 75ms before any data transfer.

### TCP vs UDP Comparison
| Feature | TCP | UDP |
| :--- | :--- | :--- |
| **Connection** | Connection-oriented (handshake required). | Connectionless — send and forget. |
| **Reliability** | ✅ Guaranteed delivery. Lost packets are retransmitted. | ❌ No guarantee. Packet loss is silent. |
| **Ordering** | ✅ In-order delivery guaranteed. | ❌ Packets may arrive out of order. |
| **Flow Control** | ✅ Sliding window prevents receiver buffer overflow. | ❌ None. |
| **Congestion Control** | ✅ Reduces send rate when network is congested. | ❌ None. Can saturate the network. |
| **Speed/Overhead** | Slower — headers, state tracking, ACKs. | Faster — minimal 8-byte header. |
| **Head-of-Line Blocking** | ✅ Later packets blocked by one lost packet. | ❌ No blocking — missing packets just skipped. |

### When to Use Which
| Protocol | Use Cases | Reason |
| :--- | :--- | :--- |
| **TCP** | HTTP/HTTPS, Databases, File Transfer, Email. | Correctness > Speed. A missing byte in a web page or SQL query is catastrophic. |
| **UDP** | Video Streaming, Voice/Video Calls, Online Games, DNS Lookups, DHCP. | Speed > Correctness. A dropped frame in a video is invisible; a 200ms delay is not. |
| **QUIC (UDP-based)** | HTTP/3. | Brings TCP-like reliability to UDP to eliminate Head-of-Line blocking at the transport layer. |

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `TCP Retransmission Rate`: High rate = packet loss / network congestion → latency spikes.
    *   `TIME_WAIT Socket Count`: Large numbers of `TIME_WAIT` sockets indicate high connection churn; tune `SO_REUSEADDR` or use connection pooling.
*   **Blast Radius (The "Impact"):**
    *   **TCP SYN Flood DDoS:** Attacker sends millions of SYN packets, exhausting server's half-open connection table. Mitigation: SYN cookies.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Saying "TCP is always better because it's reliable" — for DNS queries, the handshake cost is bigger than the entire query payload; UDP is the correct choice.
*   Not knowing that HTTP/3 uses QUIC (UDP) specifically to solve TCP's **Head-of-Line Blocking** at the transport layer (one dropped packet stalls all streams multiplexed over that TCP connection).

### Interview Tip (The "Strong Hire" Signal)
> *"Our live video streaming service uses UDP at the transport layer. A retransmitted video frame from 500ms ago is useless — we'd rather decode the next frame with the artifact. We handle reliability at the application layer with selective FEC (Forward Error Correction), not TCP retransmission."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

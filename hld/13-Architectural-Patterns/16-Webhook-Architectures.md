# ⚡ 16 - Webhook Architectures

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C125 |
| **Category** | Architectural Patterns |
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
*   **Two-Sentence Trigger:** A webhook architecture is a pattern that enables real-time, asynchronous, push-based communication from an API provider to external consumer applications by delivering HTTP POST requests containing event payloads immediately upon state changes. Developers implement webhook systems when their platform must notify clients about specific events (e.g., payment completion, order delivery, code repository commits) without forcing clients to execute continuous polling cycles, which saves system compute and network bandwidth.
*   **Scalability Dimension:** Primary: **Outbound Request Throughput** and delivery latency. Secondary: **Retry queue capacity** and database connection pooling (when retrieving payload metadata for delivery).

---

## ⚖️ 2. Trade-offs & Deep Dive

### Webhook Delivery Pipeline Architecture
A production-grade Webhook engine must handle unpredictable client failures, protect internal resources from outbound socket starvation, and guarantee security.

```
[Event Generator]
       │ (State change occurs)
       ▼
 ┌───────────┐      ┌──────────────┐      ┌─────────────┐
 │ Event Bus │ ───► │ Worker Pool  │ ───► │ Rate Limiter│ (Checks client-specific limits)
 └───────────┘      └──────────────┘      └──────┬──────┘
                          │                      │ (Under limit)
                          ▼                      ▼
                    ┌──────────────┐      ┌─────────────┐
                    │ Sign & Hash  │ ───► │ Outbound IP │
                    │ (HMAC-SHA256)│      │ NAT Gateway │
                    └──────────────┘      └──────┬──────┘
                                                 │
                                                 │ HTTP POST
                                                 ▼
                                        [Client Endpoint]
                                                 │ (Fails: 503 / Timeout)
                                                 ▼
                                          ┌─────────────┐      ┌─────────────────────┐
                                          │ Retry Queue │ ───► │ Dead Letter Queue   │
                                          │  (Backoff)  │      │ (Customer Dashboard)│
                                          └─────────────┘      └─────────────────────┘
```

#### Key Architecture Components
1.  **Decoupled Message Broker:** The event generator sends events to a message queue/log (e.g., Apache Kafka or RabbitMQ) instead of making direct HTTP calls. This isolates the main transaction database from client network failures.
2.  **Outbound Rate Limiter:** A worker must not DDoS client endpoints during event spikes. The pipeline checks a client-specific Token Bucket rate limiter (e.g., hosted in Redis) before sending the request.
3.  **Cryptographic Signing Engine:** To prevent spoofing, the webhook broker signs the body of the HTTP request using a SHA256 HMAC based on a shared secret generated for each client. The signature and a timestamp are passed in the HTTP headers:
    *   `X-Signature-256: hmac_sha256(payload, secret)`
    *   `X-Timestamp: 1684873892` (to prevent replay attacks)
4.  **Resilient Retry Loop with Backoff:** If a client server is down or returns a non-2xx status code, the worker schedules a retry. Retries must follow an exponential backoff formula (e.g., $t = 2^{attempt} \times base$) with random jitter to prevent "thundering herd" patterns against recovering client servers.
5.  **Dead Letter Queue (DLQ):** After reaching a maximum retry count (e.g., 5-10 attempts over 24 hours), the event is placed in a DLQ. The system stops processing future webhooks for that specific client if their failure rate crosses a threshold (circuit breaking) and exposes the DLQ payload on a dashboard for manual remediation.

---

### Communication Options Comparison
| Protocol | Direction | Initiation | Resource Overhead (Server) | Delivery Latency | Use Case |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Webhooks** | Server ──► Client | Push (Event-driven) | Low (Short-lived HTTP POST requests). | Low (Seconds/Millisecond queue delay). | System-to-system integrations (Stripe, GitHub). |
| **Polling** | Client ──► Server | Pull (Scheduled) | High (Vast majority of requests return 304 Not Modified). | High (Dependent on polling frequency). | Legacy systems without real-time hooks. |
| **WebSockets** | Bidirectional | Client-initiated | High (Maintains long-lived TCP sockets in memory). | Real-time (Milliseconds). | Chat apps, live gaming, collaborative workspaces. |
| **SSE (Server-Sent Events)** | Server ──► Client | Client-initiated | High (Keeps HTTP connection open indefinitely). | Real-time (Milliseconds). | Live stock tickers, activity feeds. |

---

## 💥 3. Resiliency & Operations

*   **Observability (The "Signal"):**
    *   `Outbound Latency (Queue Lag)`: Time elapsed between event generation and HTTP request initiation. A lag > 5 seconds suggests the worker pool is exhausted.
    *   `Webhook Failure Rates (by HTTP Status)`: Monitoring 5xx (client server error) vs 4xx (misconfiguration or signature mismatch).
    *   `IP NAT Port Exhaustion`: Since workers make outbound TCP connections, track outbound NAT gateway port utilization.
*   **Blast Radius (The "Impact"):**
    *   A single client hosting an extremely slow receiver endpoint (e.g., taking 30 seconds to respond) can tie up all worker threads (Thread Exhaustion), stalling delivery for all other clients.
    *   *Mitigation:* Enforce strict HTTP timeouts (e.g., max 5 seconds) on outbound requests, isolate worker pools by tier, and use non-blocking HTTP clients (event-driven networking, e.g., Netty or Go HTTP Client).

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   **Synchronous Webhook Delivery:** Designing the system to make HTTP calls to client endpoints directly inside the web request thread (blocking user-facing operations).
*   **Lack of Replay Protection:** Sending signature verification without a timestamp header. An eavesdropper can intercept the request and replay it to the client's endpoint, even without knowing the secret key.
*   **Missing Idempotency Recommendations:** Assuming delivery is always exactly-once. Because networks fail during ACKs, webhooks must be treated as **at-least-once**, and the client must be advised to store an `event_id` to guarantee idempotency.

### Interview Tip (The "Strong Hire" Signal)
> "In designing our webhook dispatch engine, we decouple the delivery using Kafka and a Redis-backed token bucket rate limiter to protect our clients from outbound DDoS conditions. To prevent resource exhaustion from slow clients, our worker nodes use non-blocking asynchronous HTTP clients with a strict 3-second connect-and-read timeout. We guarantee security by signing payloads with HMAC-SHA256, adding a timestamp header to prevent replay attacks, and exposing a Dead Letter Queue (DLQ) through a dashboard so clients can manually retry failed events after repairing their endpoints."

---

## 💡 5. My Custom Study Notes & Whiteboard

### Payload Verification: Client-Side Implementation (Python)
Below is the production-ready code showing how the client verifies incoming webhooks to ensure authenticity and prevent replay attacks.

```python
import hmac
import hashlib
import time
from typing import Dict

# Configured client secret key
WEBHOOK_SECRET = b"whsec_super_secret_key_from_dashboard_123"
MAX_ALLOWED_AGE_SECONDS = 300  # 5 minutes replay protection window

class WebhookVerificationException(Exception):
    pass

def verify_webhook_payload(payload_bytes: bytes, headers: Dict[str, str]) -> bool:
    """
    Verifies that the webhook payload came from the authorized provider
    and was not modified in transit or replayed.
    """
    signature_header = headers.get("X-Signature-256")
    timestamp_header = headers.get("X-Timestamp")

    if not signature_header or not timestamp_header:
        raise WebhookVerificationException("Missing signature or timestamp headers")

    # 1. Prevent Replay Attacks: Verify timestamp age
    try:
        request_timestamp = int(timestamp_header)
    except ValueError:
        raise WebhookVerificationException("Invalid timestamp format")

    current_timestamp = int(time.time())
    if abs(current_timestamp - request_timestamp) > MAX_ALLOWED_AGE_SECONDS:
        raise WebhookVerificationException("Webhook request expired (possible replay attack)")

    # 2. Compute local HMAC signature
    # Concatenate timestamp and raw body payload to prevent tampering with headers
    signature_payload = f"{timestamp_header}.".encode("utf-8") + payload_bytes
    
    computed_signature = hmac.new(
        key=WEBHOOK_SECRET,
        msg=signature_payload,
        digestmod=hashlib.sha256
    ).hexdigest()

    # 3. Time-Constant comparison to prevent timing attacks
    if not hmac.compare_digest(computed_signature, signature_header):
        raise WebhookVerificationException("Invalid signature")

    return True

# Example Usage
if __name__ == "__main__":
    # Simulated Raw Request Data
    body = b'{"event":"payment.succeeded","data":{"amount":5000,"currency":"usd"}}'
    current_time = str(int(time.time()))
    
    # Provider signing step
    prov_payload = f"{current_time}.".encode("utf-8") + body
    prov_sig = hmac.new(WEBHOOK_SECRET, prov_payload, hashlib.sha256).hexdigest()
    
    headers = {
        "X-Signature-256": prov_sig,
        "X-Timestamp": current_time
    }
    
    # Client Verification
    try:
        if verify_webhook_payload(body, headers):
            print("Webhook verification succeeded! Payload is authentic.")
    except WebhookVerificationException as e:
        print(f"Webhook verification failed: {e}")
```

# ⚡ 07 - Idempotency

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C122 |
| **Category** | Distributed Resiliency |
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
*   **Two-Sentence Trigger:** Idempotency is a distributed systems property where executing an operation multiple times produces the exact same side-effect and response as executing it a single time. It is crucial for network retry safety, ensuring that if a client retries a timeout request (e.g., payment creation), the system does not execute duplicate updates.
*   **Scalability Dimension:** Primary: **Data Consistency** & **System Fault Tolerance / Retry Safety**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### The Idempotency Key Design (State Machine)
1. **Request Reception:** Client generates a unique `Idempotency-Key` (e.g., UUIDv4) and sends it in HTTP headers.
2. **Lock Acquisition:** Server writes key to Redis/DB with a unique constraint and a state of `PROCESSING`. If the key already exists and status is `PROCESSING`, reject request (concurrent request lock).
3. **Execution & Commit:** Server executes transaction, saves the transaction response in the DB associated with the key, and updates status to `COMPLETED`.
4. **Retry Handling:** If client retries due to a network timeout, server reads key from DB, sees status is `COMPLETED`, and returns the cached response immediately without executing the business logic again.

```
Client ──[POST /charge Header: Idempotency-Key=XYZ]──> API Gateway
                                                           │
                                               [Check Redis for Key XYZ]
                                                           │
                                        ┌──────────────────┴──────────────────┐
                                  (Key Found)                          (Key Not Found)
                                        │                                     │
                             Return Cached Response                   Create Lock & Run
                                                                      Save Response to Redis
```

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Duplicate Request Filter Rate`: Metrics on how many incoming requests hit the idempotency cache. High rates indicate active network retries or client retry loops.
*   **Blast Radius (The "Impact"):**
    *   If the idempotency cache (Redis) goes offline or experiences connection timeouts, the server might process requests without validation, leading to duplicate writes (e.g., double charging a customer).

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Believing HTTP `GET`, `PUT`, and `DELETE` calls are naturally idempotent without verifying the backend implementation (if your `DELETE` query decrements a counter instead of removing a row ID, it is not idempotent).
*   Not locking the key during execution (leads to race conditions if two threads process the same key concurrently).

### Interview Tip (The "Strong Hire" Signal)
> *"To ensure retry safety on our payment checkout, we mandate an `Idempotency-Key` header. We acquire a distributed lock in Redis for the key. If the lock is held, concurrent retries block. If the operation has completed, we return the cached payment receipt, preventing double charges."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

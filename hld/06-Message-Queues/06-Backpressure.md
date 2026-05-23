# ⚡ 06 - Backpressure

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C097 |
| **Category** | Messaging & Queues |
| **Difficulty** | 🟡 Medium |
| **Interview Frequency** | 🟡 Medium |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Backpressure is a flow-control mechanism where a slow consumer signals to its upstream producer to slow down message production, preventing the consumer's buffer from overflowing and crashing. Without backpressure, a fast producer overwhelms a slow consumer, causing queue overflow, OOM errors, message loss, or cascading system failure.
*   **Scalability Dimension:** Primary: **Consumer Buffer Protection** & **Cascading Failure Prevention from Speed Mismatch**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### The Speed Mismatch Problem
```
Producer: 10,000 messages/second
Consumer: 1,000 messages/second
Buffer:   10 million messages capacity

Without backpressure:
  t=0:     Buffer = 0 messages
  t=100s:  Buffer = 10M messages (FULL)
  t=101s:  Buffer overflow → messages DROPPED or producer blocked
  t=200s:  OOM crash or cascading failure
```

### Backpressure Strategies
| Strategy | How It Works | Trade-off |
| :--- | :--- | :--- |
| **Block / Slow Producer** | Consumer sends a "slow down" signal. Producer blocks or reduces send rate. | ✅ No message loss. ❌ Producer latency increases. |
| **Drop Messages** | When buffer is full, new incoming messages are dropped. | ✅ System stays stable. ❌ Data loss (only acceptable for non-critical metrics). |
| **Bounded Queue + Back-Pressure Signal** | Queue has a fixed max size. When full, producer gets a `QUEUE_FULL` error and backs off (using Exponential Backoff). | ✅ Bounded memory. ✅ No OOM. ❌ Retry overhead. |
| **Scale Consumers** | Auto-scale consumer fleet when queue depth exceeds threshold. | ✅ No slowing of producer. ❌ Scale lag (takes 60–120s for new instances to start). |

### Reactive Systems & Backpressure (RxJava / Project Reactor)
In reactive/streaming systems, backpressure is a first-class citizen:
*   `Flux.onBackpressureDrop()` — drop messages when downstream is slow.
*   `Flux.onBackpressureBuffer(N)` — buffer up to N items before applying backpressure.
*   `Flux.onBackpressureError()` — throw error to upstream when buffer full.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Queue Depth Growth Rate`: Accelerating growth = consumer cannot keep up. This is the **backpressure alarm**.
    *   `Producer Send Blocked Time`: Time producer spends blocked waiting for the queue to accept messages.
*   **Blast Radius (The "Impact"):**
    *   Unbounded queues without backpressure → OOM crash on the broker or consumer → full system outage.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Using unbounded queues without a configured max size (the queue silently grows until OOM crash, with no early warning).
*   Thinking "just add more consumers" solves backpressure without considering consumer scale-out latency (during the 2-minute scale window, the queue is still growing).

### Interview Tip (The "Strong Hire" Signal)
> *"Our video transcoding pipeline uses a bounded SQS queue. When the queue depth exceeds 10k messages, our CloudWatch alarm triggers auto-scaling to add transcoding workers. We set SQS `MaxReceiveCount=3` + DLQ so that jobs failing after 3 retries go to a DLQ for investigation rather than re-queuing indefinitely and adding to the backpressure."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

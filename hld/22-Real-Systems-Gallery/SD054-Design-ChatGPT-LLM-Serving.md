# 🏢 SD054 - Design ChatGPT / LLM Serving Infrastructure

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Problem ID** | SD054 |
| **Category** | AI Infrastructure |
| **Difficulty** | 🔴 Expert |
| **Interview Frequency** | 🔥 Must Know (2024–2026) |
| **Target Companies** | OpenAI, Anthropic, Google, Meta, ByteDance |
| **SDE-2 Mandatory** | ✅ Yes |
| **Status** | Completed |
| **Times Practiced** | 1 |
| **Last Practiced** | 2026-05-24 |
| **Next Review** | 2026-06-24 |
| **Confidence** | 🟢 Applied |
| **Mastery** | 🟢 Expert |

---

## 📋 1. Core Requirements & Scale

### Functional Requirements
- Users can send prompts and receive token-by-token streaming responses.
- System must persist chat history (conversational memory) and append new turns.
- System must support conversational context truncation (sliding context window).
- System must rate limit requests based on tokens per minute (TPM) and requests per minute (RPM).

### Non-Functional Requirements
- **Low Time-To-First-Token (TTFT)**: First token delivered in < 100ms.
- **High Token Throughput**: Output generation rate > 30 tokens/second per user.
- **High Availability & Fault Tolerance**: Fallback to backup model providers or offline caches.
- **Cost Efficiency**: Optimize GPU VRAM utilization.

### Scale Targets (Back-of-the-Envelope)
- **Daily Active Users (DAU)**: 50M.
- **Total Requests/Day**: 250M.
- **Average Prompt Length**: 500 tokens input, 500 tokens output.
- **Write throughput (Ingest)**: ~3,000 RPS.
- **VRAM Constraint**: Model parameters (e.g., Llama-70B FP16 requires 140GB VRAM just for weights).

---

## 📐 2. High-Level Architecture

```
                                [ Client Browser / App ]
                                           │
                        (Server-Sent Events Token Stream)
                                           ▼
                                    [ Load Balancer ]
                                           │
                                           ▼
                                    [ API Gateway ] ◄───────► [ Redis Cluster (TPM/RPM Rate Limiting) ]
                                           │
                                           ▼
                                [ Orchestrator Service ] ◄──► [ DynamoDB + Cache (Session History) ]
                                           │
                                           ▼
                             [ LLM Inference Router / Queue ]
                                           │
                                           ▼
                              [ GPU Worker Node Cluster ]
                        (Continuous Batching + PagedAttention)
                                     │            │
                                     ▼            ▼
                               [ KV Cache ]   [ Model Weights (VRAM) ]
```

---

## ⚖️ 3. Deep Dive & Core Components

### A. Server-Sent Events (SSE) vs. WebSockets for Streaming
For streaming text tokens back to the user, **SSE** is highly preferred over WebSockets:
* **SSE**: Runs over HTTP/2, uses standard HTTP connections, is unidirectional (server to client), and supports automatic reconnection. Since conversational chats are mostly request-response (prompt up, stream down), SSE avoids the handshake and connection maintenance overhead of WebSockets.
* **WebSockets**: Bi-directional, stateful, and more complex. Only required if building multi-user real-time whiteboard chats.

### B. The VRAM Bottleneck: KV Cache & PagedAttention
During LLM generation, the keys ($K$) and values ($V$) of all previous tokens in the sequence are cached to avoid recomputation. This **KV Cache** grows linearly with the context length and batch size, quickly saturating GPU memory.
* **The Problem**: Traditional frameworks pre-allocate contiguous memory blocks for the maximum context size (e.g., 8k tokens), leading to **up to 60-80% memory waste** (internal fragmentation).
* **The Solution**: **PagedAttention** (used in engines like vLLM) partitions the KV cache into small, non-contiguous physical blocks (akin to virtual paging in OS). A block table maps logical token slots to physical memory, allowing the engine to utilize near 100% of GPU memory and fit 2-4x more concurrent batches on the same hardware.

### C. Continuous Batching (Iteration-level Scheduling)
Standard static batching waits for all sequences to finish before loading the next batch, wasting GPU cycles on finished short requests (padding overhead). **Continuous Batching** schedules tokens at the iteration level: as soon as one request generates an end-of-text token, it is evicted, and a new request from the queue takes its place mid-flight.

---

## 🚫 4. Common Mistakes & Interview Playbook

### Common Mistakes (The "Junior" Signals)
- Proposing standard REST polling for LLM responses (destroys user experience and creates massive request overhead).
- Forgetting that model weights and the KV Cache compete for the exact same VRAM on the GPU, failing to explain how to scale concurrent requests.
- Keeping conversational history in memory on the API Gateway, violating the stateless server rule.

### Interview Tip (The "Strong Hire" Signal)
> *"To optimize throughput, we use Continuous Batching to schedule execution at the token level, and PagedAttention to eliminate KV-cache fragmentation. For API rate limits, we use a sliding window counter in Redis to evaluate both Request-Per-Minute (RPM) and Token-Per-Minute (TPM) limits before dispatching prompts to our routing queue."*

# 🧵 Producer-Consumer Pattern — v2 (Production-Grade)

## Compilation & Run

```bash
cd "04-Advanced_Architectural/01-Concurrency-Patterns/01-Producer-Consumer/JAVA/"

javac model/*.java queue/*.java pool/*.java workers/*.java Main.java

java Main
```

---

## What's new in v2 vs v1

| Feature | v1 | v2 |
|---|---|---|
| Message type | Raw `String` | `Message` object with `correlationId` |
| Duplicate detection | ❌ None | ✅ Idempotency key store (simulates Redis) |
| Failure handling | ❌ Message silently dropped | ✅ Dead-Letter Queue with retry |
| Thread pool sizing | ❌ Hardcoded `3` | ✅ Formula-driven (CPU vs I/O) |
| Queue health | ❌ None | ✅ Depth % warning at 80% |

---

## Three Scenarios Explained

### Scenario 1 — Idempotent Consumer + DLQ
The Producer emits typed Messages. One message is intentionally re-delivered
(simulating a network re-delivery from Kafka/RabbitMQ). The Consumer
detects the duplicate via `correlationId` and skips it safely.

### Scenario 2 — CPU-Bound Pool (`N_cores + 1` threads)
Workers hash passwords using intensive computation. Adding more threads
than cores would cause destructive context-switching overhead.

```
Threads = 4 cores + 1 = 5 threads
```

### Scenario 3 — I/O-Bound Pool (`N_cores × (1 + wait/cpu)` threads)
Workers simulate a DB query (200ms wait) + ORM mapping (50ms CPU).
The high wait/cpu ratio means many threads can share the same cores.

```
Threads = 4 × (1 + 200/50) = 4 × 5 = 20 threads
```

---

## Interview Talking Points

> **"Why is the thread count different for CPU vs I/O tasks?"**
> CPU tasks keep cores busy 100%. More threads = wasted context switches.
> I/O tasks leave cores idle during waits. More threads = better CPU utilization.

> **"What is idempotency and why does it matter here?"**
> At-Least-Once delivery guarantees mean messages *will* be re-delivered on
> failure. Without idempotency an email gets sent twice, a payment charged twice.
> In production the `processedIds` Set is replaced by a `Redis SETNX` with TTL.

> **"What happens when a message can never be processed?"**
> After max retries it goes to the Dead-Letter Queue. An alert fires, an
> operator investigates, and the message can be replayed after the root cause
> is fixed. Silent drops are never acceptable in production.

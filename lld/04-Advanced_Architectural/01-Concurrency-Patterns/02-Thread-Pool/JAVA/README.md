# 🧵 Thread Pool Pattern v2 — Production-Grade

## Compilation & Run

```bash
cd "04-Advanced_Architectural/01-Concurrency-Patterns/02-Thread-Pool/JAVA/"

javac task/*.java pool/*.java Main.java

java Main
```

---

## v1 → v2: What Changed and Why

| Problem in v1 | Fix in v2 | Why it Matters |
|---|---|---|
| Busy-wait spin loop (`while !isTerminated`) | `awaitTermination(10, SECONDS)` | v1 burned a full CPU core doing nothing. v2 releases the CPU entirely. |
| `Runnable` — exceptions silently swallowed | `Callable<String>` + `Future` | v1 hid failures. v2 surfaces them to the caller. |
| No task timeout — runaway threads possible | `future.get(3, SECONDS)` + `cancel(true)` | A DB-leaked task can no longer hold a thread forever. |
| Unbounded queue — OOM under surge | `ArrayBlockingQueue(4)` + rejection handler | v2 returns HTTP 503 at 7 in-flight tasks instead of crashing. |
| Zero visibility | `beforeExecute`/`afterExecute` hooks + `printMetrics()` | v2 shows active threads, queue depth, and total time. |

---

## Three Scenarios Explained

### Scenario 1 — Normal workload
5 tasks submitted. All succeed. Each `Future.get()` is called with a
3-second timeout so no single task can block the calling thread forever.

### Scenario 2 — Runaway Task
A task simulating a 30-second DB leak is submitted. `future.get(3s)` times
out and calls `future.cancel(true)` which sends an interrupt signal to the
thread, freeing it for the next task.

### Scenario 3 — Queue Overflow
8 tasks flood a pool with 3 threads + queue of 4 = max 7 in-flight.
Task #8 hits the rejection handler and logs an HTTP 503 warning instead
of crashing the JVM with `OutOfMemoryError`.

---

## Interview Talking Points

> **"Why switch from Runnable to Callable?"**
> `Runnable.run()` is fire-and-forget. If it throws a RuntimeException
> the pool discards it silently. `Callable` returns a `Future` which
> carries both the result AND any exception, making failures visible.

> **"What is the danger of an unbounded task queue?"**
> `Executors.newFixedThreadPool` uses a `LinkedBlockingQueue` with no
> size limit by default. Under sustained overload, the queue grows until
> the JVM runs out of heap and throws `OutOfMemoryError`. The fix is a
> bounded `ArrayBlockingQueue` with an explicit rejection policy.

> **"How does Spring Boot configure its thread pool?"**
> Via `ThreadPoolTaskExecutor` which maps directly to `ThreadPoolExecutor`.
> The defaults in `application.properties` are:
> `spring.task.execution.pool.core-size=8`
> `spring.task.execution.pool.max-size=16`
> `spring.task.execution.pool.queue-capacity=100`

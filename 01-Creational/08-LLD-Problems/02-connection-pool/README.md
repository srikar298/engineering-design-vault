# 🗄️ LLD Problem: Database Connection Pool

> **Patterns:** Singleton · Object Pool

---

## 📋 Problem Statement

Design a **Database Connection Pool** for a high-throughput web application. Requirements:

1. The application has hundreds of concurrent threads — each needs DB access
2. Creating a new DB connection costs 50-200ms (TCP handshake + authentication)
3. The DB server allows a **maximum of N connections** simultaneously (e.g., PostgreSQL default = 100)
4. Connections must be **reused** after each use, never re-created unnecessarily
5. **Only one pool** should exist per database endpoint — two pools would double the connections and potentially exceed the DB's max limit

---

## 🧩 Pattern Mapping

| Sub-Problem | Pattern | Why |
|---|---|---|
| Only one pool per DB endpoint | **Singleton** | Two pools = double connections = DB overload. The pool's in-use tracking must be globally consistent |
| Reusing expensive connections instead of creating new ones | **Object Pool** | Creating a connection costs 50-200ms. Pool keeps N pre-warmed connections and recycles them |

---

## 🏗️ Architecture

```mermaid
classDiagram
    class ConnectionPool {
        <<Singleton - Bill Pugh>>
        -pool: List~DatabaseConnection~
        -maxSize: int
        +getInstance()$ ConnectionPool
        +acquire() DatabaseConnection
        +release(DatabaseConnection)
        +getFreeCount() int
    }

    class DatabaseConnection {
        <<Pooled Resource>>
        -id: int
        -inUse: boolean
        -lastUsed: Instant
        +query(String sql)
        ~markInUse()
        ~markFree()
    }

    class InstanceHolder {
        <<static inner>>
        -INSTANCE: ConnectionPool
    }

    ConnectionPool *-- DatabaseConnection : manages pool of
    ConnectionPool +-- InstanceHolder : holds singleton
```

---

## 💻 Code Walk-Through

### Singleton: pool created once, used everywhere
```java
// Thread 1:
ConnectionPool pool = ConnectionPool.getInstance();  // Creates pool with 5 connections

// Thread 2 (different class, different thread):
ConnectionPool pool = ConnectionPool.getInstance();  // Returns the SAME pool
// pool.getFreeCount() will reflect connections acquired by Thread 1
```

### Object Pool: acquire → use → release
```java
DatabaseConnection conn = pool.acquire();    // Get a free connection (no new creation!)
try {
    conn.query("SELECT * FROM orders");      // Use it
} finally {
    pool.release(conn);                      // MUST release — or pool exhausts!
}
```

### The Performance Difference

| Approach | 1000 requests | Per-request cost |
|---|---|---|
| Create new connection each time | 1000 × 100ms = 100 seconds | 100ms |
| Connection Pool (5 connections) | ~5 × 100ms warmup + reuse | ~0.1ms per acquire |

### How to Run
```bash
cd JAVA/
javac pool/*.java Main.java
java Main
```

---

## 🎭 Junior vs. Senior

| Concern | Junior | Senior |
|---|---|---|
| **Thread safety** | No synchronization on `acquire()` → two threads get the same connection | `synchronized` on `acquire()` and `release()` |
| **Connection leak** | `acquire()` without `finally { pool.release(conn) }` → pool exhaustion in production | Always use try-finally or AutoCloseable pattern |
| **Pool exhaustion** | Throws immediately with a confusing NPE | Throws `IllegalStateException("All connections in use — consider increasing pool size")` |
| **Singleton** | Creates pool in every service class separately | One pool, injected via DI or accessed via `getInstance()` |

---

## 🧠 FAANG Interview Angles

**Q: How does this differ from HikariCP (production connection pool)?**
> HikariCP adds: configurable max-pool-size, connection timeout/wait (instead of instant throw), health checks (auto-reconnect on stale connections), connection lifetime limits, and metrics. Our implementation is the conceptual skeleton — HikariCP is the production-hardened version.

**Q: What's wrong with `synchronized` on the whole `acquire()` method at scale?**
> Under high concurrency, every thread blocks waiting for the lock, even when there are free connections. Production pools use lock-free data structures (`LinkedBlockingQueue<DatabaseConnection>`) so threads only block when the pool is truly exhausted, not while scanning for a free connection.

**Q: How would you make this testable (Singleton problem)?**
> Option 1: Extract `IConnectionPool` interface, mock it in tests.
> Option 2: Reset the `InstanceHolder` via reflection in tests (fragile but common).
> Option 3: Use a DI framework — Spring `@Bean(scope=singleton)` manages the instance. Tests inject a mock pool.

**Q: What's the difference between Object Pool and Flyweight pattern?**
> **Object Pool:** Large, stateful, expensive objects (DB connections). Objects are reused sequentially by one thread at a time — returned, then given to the next.
> **Flyweight:** Small, immutable, shared objects (Character objects in a text editor). Multiple threads read the same instance simultaneously — no acquire/release needed.

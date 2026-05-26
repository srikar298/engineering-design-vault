# Distributed Locking

| Field       | Value                          |
|-------------|--------------------------------|
| Concept ID  | C075                           |
| Category    | Distributed Systems            |
| Difficulty  | 🔴 Hard                        |
| Frequency   | 🔥 High                        |
| Tags        | `consensus`, `redis`, `zookeeper`, `etcd`, `fencing-tokens`, `redlock` |

---

## 1. The Core Concept

### The Problem — Why a Regular Mutex Doesn't Work

In a single-process application, mutual exclusion is solved by the OS. A `mutex`, `semaphore`, or `synchronized` block is managed by the kernel — it lives in shared memory accessible to all threads. When you call `lock.acquire()`, the kernel suspends competing threads on the same machine until the holder calls `lock.release()`.

Now distribute that across 50 payment-service replicas on 50 different physical machines. There is **no shared memory**. OS-level primitives don't cross network boundaries. You need a **coordinator** — a shared, network-accessible system that all processes can reach and that enforces mutual exclusion between them.

**Motivating scenarios where you absolutely need distributed locking:**
- **Idempotent payment processing**: only one of N replicas must debit a bank account for a given `payment_id`.
- **Scheduled job de-duplication**: 10 cron replicas run at midnight — only one should send the daily digest email.
- **Inventory reservation**: prevent two customers from buying the last item in stock simultaneously.
- **Leader election in a cluster**: exactly one node must act as the primary writer.

### The 3 Requirements of a Distributed Lock

1. **Mutual Exclusion** — At any given moment, at most one process holds the lock.
2. **Deadlock Freedom** — Even if the lock-holding process crashes, the lock is eventually released. No lock is held forever.
3. **Fault Tolerance** — The locking service itself must remain available even when some of its nodes fail.

```
Simple Distributed Lock Flow:

Process A           Lock Service          Process B
    |                    |                    |
    |---- ACQUIRE ------>|                    |
    |<--- GRANTED -------|                    |
    |                    |                    |
    | [holds lock]       |<--- ACQUIRE -------|
    |                    |---- WAIT/REJECT -->|
    |                    |                    |
    |---- RELEASE ------>|                    |
    |                    |--- GRANTED ------->|
    |                    |    [B now holds]   |
```

---

## 2. Deep Dive — The Four Approaches

### Approach 1 — Redis SETNX (Simple, but Subtly Wrong)

The naive approach: use Redis as the lock store. Redis is fast, widely deployed, and supports atomic operations.

**The command:**
```bash
SET lock_key <uuid> NX EX 30
# NX = only set if Not eXists
# EX 30 = expire in 30 seconds (auto-release)
# uuid = unique identifier for this lock holder
```

**Why UUID matters for release:**
Without a unique value, Process A could accidentally release Process B's lock. Always compare before deleting:

```lua
-- Atomic Lua script in Redis
if redis.call("GET", KEYS[1]) == ARGV[1] then
    return redis.call("DEL", KEYS[1])
else
    return 0
end
```

**Flow:**
```
Process A                         Redis (single node)
    |                                   |
    |-- SET lock NX EX 30 uuid_A ------>|
    |<-- OK (lock acquired) ------------|
    |                                   |
    | [do critical work]               |
    |                                   |
    |-- GET lock; if uuid_A → DEL ----->|
    |<-- lock released -----------------|
```

**Problems with Single-Node Redis SETNX:**

| Problem | Description | Impact |
|---------|-------------|--------|
| **Clock skew** | Different servers have slightly different clocks. TTL-based expiry can behave unexpectedly across nodes. | Minor |
| **Process GC pause** | Process holds lock, JVM GC runs for 35s, lock expires (TTL=30s), another process acquires. First process resumes, believes it still holds the lock. Both now execute critical section. | **CRITICAL** |
| **Redis failover gap** | Redis master crashes. Sentinel promotes a replica. The `SET` command from Process A hadn't replicated yet. Process B asks the new master — lock doesn't exist — B acquires. A and B both hold the lock. | **CRITICAL** |
| **Network partition** | Redis becomes unreachable. No process can acquire. System is deadlocked until Redis recovers. | Availability risk |

---

### Approach 2 — Redlock (Distributed Redis)

Proposed by Salvatore Sanfilippo (Redis creator) to solve the single-master SPOF. The algorithm uses **N independent Redis nodes** (typically N=5) with no replication between them.

**Algorithm:**
1. Record `start_time = now()`.
2. Try to acquire the lock on all N nodes in sequence using `SET NX EX`. Use a **small timeout** (e.g., 5-50ms) per node to avoid blocking on a slow node.
3. The lock is considered acquired if and only if:
   - You acquired it on **N/2 + 1 nodes** (majority quorum).
   - Total elapsed time < lock TTL.
4. Effective TTL = original TTL - elapsed acquisition time.
5. **Release**: send `DEL` to **all** nodes (even those where you failed to acquire).

```
Redlock Acquisition (N=5 nodes):

Process A               R1   R2   R3   R4   R5
    |                   |    |    |    |    |
    |-- SET NX EX 30 -->|    |    |    |    |
    |<-- OK ------------|    |    |    |    |
    |-- SET NX EX 30 ------->|    |    |    |
    |<-- OK ------------------|    |    |    |
    |-- SET NX EX 30 ------------->|    |    |
    |<-- FAIL (already set) --------|    |    |
    |-- SET NX EX 30 ------------------->|    |
    |<-- OK -----------------------------|    |
    |-- SET NX EX 30 ------------------------>|
    |<-- OK -----------------------------=----|
    |                   |    |    |    |    |
    | Acquired on R1, R2, R4, R5 = 4 of 5  |
    | 4 >= 3 (majority) → LOCK GRANTED      |
    | Effective TTL = 30s - acquisition_time |
```

**Redlock Critique — Martin Kleppmann (2016)**

Martin Kleppmann published "How to do distributed locking" on his blog, arguing Redlock is **not safe** even under normal conditions. His core argument:

> Distributed locks exist to protect a resource. If the lock doesn't come with a fencing token, it's not actually safe.

**The GC Pause Attack on Redlock:**
```
Timeline of disaster:

t=0   Process A acquires Redlock. Effective TTL = 25s.
t=1   Process A is about to write to storage.
t=2   JVM GC pause starts on Process A's machine.
t=27  Lock expires (25s elapsed).
t=28  Process B acquires Redlock on the same key.
t=29  Process B writes to storage. [B's write is valid]
t=30  GC pause ends. Process A resumes.
t=31  Process A also writes to storage. [CORRUPTION: A overwrites B's write]
```

Even with Redlock across 5 nodes, this scenario is theoretically possible with sufficiently long GC pauses or network delays. **Only fencing tokens solve this.**

---

### Approach 3 — ZooKeeper Ephemeral Nodes (The Gold Standard)

Apache ZooKeeper is a distributed coordination service built on **ZAB** (ZooKeeper Atomic Broadcast), a Paxos-like consensus protocol. It provides sequential consistency guarantees that Redis cannot.

**Key ZooKeeper concepts for locking:**

| Concept | Description |
|---------|-------------|
| **znode** | A node in ZooKeeper's tree-structured namespace (like a file system path) |
| **Ephemeral znode** | Auto-deleted when the client session that created it disconnects or times out |
| **Sequential znode** | ZooKeeper appends a monotonically increasing counter to the znode name |
| **Watcher** | A one-time notification triggered when a znode changes |

**Lock acquisition algorithm:**
```
1. Create ephemeral sequential znode:  /locks/payment-lock-0000000012
2. List all children of /locks
3. If your znode has the smallest sequence number → you hold the lock.
4. Otherwise, WATCH the znode with the next-smallest number.
5. When that znode is deleted → go to step 2.
```

```
ZooKeeper Distributed Lock Sequence:

ZooKeeper namespace:
/locks/
  ├── payment-lock-0000000010  ← Process A (holds lock)
  ├── payment-lock-0000000011  ← Process B (watches 0010)
  └── payment-lock-0000000012  ← Process C (watches 0011)

Process A completes work:
  - Deletes /locks/payment-lock-0000000010

ZooKeeper notifies Process B (0010 deleted):
  - Process B re-lists children
  - 0011 is now smallest → Process B holds the lock

Process B completes work OR crashes:
  - Ephemeral node 0011 auto-deleted (session timeout if crash)
  - Process C gets notified → acquires lock
```

**Why ZooKeeper is safer than Redis:**

1. **No TTL needed**: Ephemeral nodes are tied to ZooKeeper sessions, not wall-clock time. If a process crashes, its session eventually expires (session timeout, configurable), and the ephemeral node is deleted. No risk of clock skew.
2. **Sequential consistency via ZAB**: All writes go through a leader and are linearizable. There's no "write not yet replicated" window like Redis replication.
3. **Watch mechanism prevents thundering herd**: Processes watch only their predecessor — not all processes wake up when the lock is released.
4. **Fencing tokens built-in**: The sequential counter IS a fencing token.

**Cost:** ZooKeeper is operationally heavier than Redis. Requires a ZooKeeper ensemble (3 or 5 nodes). Latency is higher (milliseconds vs. sub-millisecond for Redis).

---

### Approach 4 — etcd + Leases (Kubernetes Standard)

`etcd` is a distributed key-value store that uses the **Raft** consensus algorithm. It's the backbone of Kubernetes (stores all cluster state) and is used for leader election by the Kubernetes control plane itself.

**etcd locking primitives:**

| Primitive | Description |
|-----------|-------------|
| **Lease** | A TTL-based grant. Key-value pairs attached to a lease are auto-deleted when the lease expires. Processes renew leases via keepalive heartbeats. |
| **Transactions (STM)** | Compare-and-swap operations. `if (key == expected_val) { put(key, new_val) }`. Atomic. |
| **Election** | High-level abstraction built on top of leases and STM for leader election. |

**etcd lock flow:**
```
Process A                          etcd cluster (3 nodes, Raft)
    |                                       |
    |-- GrantLease(TTL=30s) --------------->|
    |<-- LeaseID: 12345 --------------------|
    |                                       |
    |-- Put(/locks/payment, A, LeaseID=12345) ->|
    |<-- Revision: 47 (Raft-committed) -----|
    |                                       |
    | [Start keepalive goroutine]           |
    | [Every 10s: KeepAlive(LeaseID=12345)] |
    |                                       |
    | [Critical work...]                    |
    |                                       |
    |-- Delete(/locks/payment) ------------>|
    |<-- Revision: 48 ----------------------|
```

**If Process A crashes:**
- Keepalive heartbeats stop.
- After TTL expires, etcd auto-revokes the lease.
- The key `/locks/payment` is auto-deleted.
- Process B (watching the key) is notified and acquires.

**etcd vs ZooKeeper for locking:**

| Dimension | etcd | ZooKeeper |
|-----------|------|-----------|
| Consensus algorithm | Raft | ZAB (Paxos-like) |
| API | gRPC | Custom TCP protocol |
| Language integration | Go-native, good clients for all | Java-native, varied quality |
| Operational complexity | Moderate | High |
| Kubernetes usage | Core (all cluster state) | Optional (replaced by etcd) |
| Watch/notify | gRPC streams | Watcher callbacks |
| Lease auto-renewal | Built-in keepalive | Session heartbeats |

---

## 3. The GC Pause Problem and Fencing Tokens

This is the most important concept for SDE-2+ interviews. Even with a perfectly implemented distributed lock, you still need fencing tokens for true safety.

### The Scenario

```
Timeline of Doom (even with "correct" distributed lock):

T=0   Process A acquires distributed lock. Gets token #33.
T=1   Process A: "I'm going to write to database with token #33."
T=2   OS decides to page out Process A's memory. Process A pauses.
      -- OR --
      JVM Full GC pause begins. Process A is stopped.
      -- OR --
      Network packet to database is delayed by 60 seconds.
T=35  Lock TTL expires.
T=36  Process B acquires lock. Gets token #34.
T=37  Process B writes to database. Database accepts token #34.
T=38  Process A's GC pause ends. Process A resumes.
T=39  Process A sends its write to database with token #33.

Without fencing: Database accepts A's write. B's write is OVERWRITTEN.
With fencing: Database REJECTS token #33 (< last seen #34). A's write is dropped. ✓
```

### How Fencing Tokens Work

```
Fencing Token Mechanism:

Lock Service              Process A            Storage Server
     |                       |                      |
     |<-- Acquire lock -------|                      |
     |-- Token: #33 -------->|                      |
     |                       |                      |
     |                       |-- Write(data, tok=33)->|
     |                       |                      |-- Store(data)
     |                       |                      |-- lastToken = 33
     |                       |                      |
     | [Lock expires, GC]    |                      |
     |                       |                      |
     |<-- Acquire lock (B) --|----                  |
     |-- Token: #34 -------->|---- (B gets #34)     |
     |                       |                      |
     |               B ------+---Write(data2, tok=34)->|
     |                       |                      |-- Store(data2)
     |                       |                      |-- lastToken = 34
     |                       |                      |
     | [A's GC ends, A resumes with tok=33]         |
     |                       |                      |
     |               A ------+---Write(dataA, tok=33)->|
     |                       |                      |-- REJECT (33 < 34)
     |                       |<-- Error: Stale token-|
```

**The storage server is the final arbiter.** It tracks the highest fencing token it has seen and rejects any request with a lower token. This works because:
- Fencing tokens are **monotonically increasing** (ZooKeeper sequential counter, etcd revision number).
- The storage server can enforce this check atomically.

**Where to get fencing tokens:**
| Lock System | Built-in Fencing Token? | Token Type |
|-------------|------------------------|------------|
| ZooKeeper ephemeral sequential | ✅ Yes | znode sequential number |
| etcd leases | ✅ Yes | etcd revision (cluster-wide monotonic counter) |
| Redis SETNX | ❌ No | Must implement separately |
| Redlock | ❌ No | Explicitly warned against by Kleppmann |

---

## 4. Approach Comparison

| Dimension | Redis SETNX | Redlock | ZooKeeper | etcd |
|-----------|-------------|---------|-----------|------|
| **Safety** | ❌ Single SPOF | ⚠️ Unsafe (GC pauses) | ✅ Strong | ✅ Strong |
| **Performance** | ✅ Sub-millisecond | ✅ Fast | ⚠️ ~1-5ms | ⚠️ ~1-5ms |
| **Ops complexity** | ✅ Low | ✅ Moderate | ❌ High | ⚠️ Moderate |
| **Fencing token** | ❌ No | ❌ No | ✅ Yes | ✅ Yes |
| **Auto-release on crash** | ✅ TTL-based | ✅ TTL-based | ✅ Session ephemeral | ✅ Lease-based |
| **Deadlock risk** | ⚠️ GC > TTL | ⚠️ GC > TTL | ✅ No (no TTL) | ⚠️ If keepalive dies |
| **Quorum reads** | ❌ No | ✅ Yes | ✅ Yes | ✅ Yes |
| **Sequential consistency** | ❌ No | ❌ No | ✅ Yes | ✅ Yes |
| **Best for** | Low-stakes best-effort | Not recommended | Mission-critical | Kubernetes-native |

---

## 5. Real-World Usage

### Redis-based Locking
- **GitHub**: Uses Redlock-style Redis locking for their job scheduling system (Resque). Acknowledged the limitations and mitigates them at the application layer.
- **Sidekiq Pro** (Ruby): Implements distributed unique jobs using Redis `SETNX`. Suitable for soft mutual exclusion (e.g., "run job at most once per 30 seconds").

### ZooKeeper-based Locking
- **Apache Kafka**: Older versions of Kafka used ZooKeeper for broker leader election and partition leader election. Each partition had one leader broker — this was enforced via ZooKeeper ephemeral nodes.
- **Apache HBase**: Uses ZooKeeper for master election and region server failure detection.
- **Apache HDFS**: NameNode high availability uses ZooKeeper for leader election to prevent split-brain.

### etcd-based Locking
- **Kubernetes**: The `kube-scheduler` and `kube-controller-manager` use etcd-backed leader election (`client-go/tools/leaderelection`). Only the elected leader actively processes work; standbys are hot standbys.
- **CoreDNS**: Uses etcd for distributed leader election in multi-replica deployments.
- **Vitess** (YouTube's MySQL sharding): Uses etcd for distributed locking of schema changes.

### Custom Implementations
- **Google Chubby**: Google's internal distributed lock service (Paxos-based). Used by Bigtable, Spanner, and GFS for leader election and small file storage. The inspiration for ZooKeeper.
- **Amazon DynamoDB**: Teams at Amazon use conditional writes (`ConditionExpression`) on DynamoDB as a distributed lock. The `version` attribute acts as a fencing token.

---

## 6. SDE-2 Interview Script

**Interviewer: "How would you implement distributed locking in your payment service?"**

---

**Opening — State the problem:**

> "Great question. Before I propose a solution, let me articulate what we're actually trying to solve. In a payment service, we might have 20 replicas running simultaneously. If two replicas both try to process the same payment — say, because the client retried — we could debit a user twice. We need exactly one replica to win. That's mutual exclusion across process boundaries, which requires a distributed lock."

**Clarify requirements:**

> "Let me ask a few things. How critical is strong mutual exclusion? Is this for financial transactions where double-charging is catastrophic, or is it for something like cache population where a redundant write is acceptable? And do we already have ZooKeeper or etcd in our stack, or are we Redis-only right now?"

**If Redis-only (common):**

> "I'd implement Redis-based locking with careful attention to two failure modes. First, I'd use `SET lock_key uuid NX EX 30` — the NX ensures only one process acquires, and the UUID is unique per lock holder. For release, I'd use an atomic Lua script: get the value, compare it to my UUID, only delete if it matches. This prevents accidentally releasing someone else's lock.

> The TTL of 30 seconds handles the crash case. But here's the critical insight I want to flag: even with a correct Redis lock, there's a GC pause problem. If our JVM pauses for 40 seconds, our lock expires, another replica acquires, and both think they hold the lock. The only real solution is fencing tokens.

> For our payment service, I'd generate a monotonically increasing token with each lock grant — I can store a counter in Redis using INCR atomically. The payment processor (or our database layer) checks: has it seen a token greater than this one? If yes, reject the request. This way, even if Process A's lock expired and B took over, A's stale write will be rejected at the resource level."

**If ZooKeeper or etcd is available:**

> "I'd strongly prefer ZooKeeper ephemeral sequential nodes or etcd leases for a payment system. Here's why: ZooKeeper's sequential znodes give us a built-in fencing token — the sequential counter is the token. Its ephemeral nature means we don't need TTLs: if our process crashes, the ZooKeeper session expires and the node is auto-deleted. There's no 'clock skew affecting TTL' risk. And ZAB consensus means writes are sequentially consistent — there's no replication lag window where two nodes disagree about who holds the lock."

**Always mention fencing tokens:**

> "Regardless of which lock mechanism we use, I'd emphasize that the lock alone is not sufficient for safety. We need the resource — our payment database — to enforce fencing tokens. Every lock grant includes a monotonically increasing token. Every write to the payments table includes that token. The database uses a conditional update: `UPDATE payments SET ... WHERE payment_id = ? AND last_token < ?`. If a stale process tries to write with an old token, the conditional fails. This is the only defense against GC pauses, which are a real concern in JVM-based services."

**Trade-offs wrap-up:**

> "In summary: Redis is operationally simple but requires careful fencing token implementation. ZooKeeper is safer but operationally heavier. etcd is a great middle ground if we're already in a Kubernetes environment. For a payment service where correctness is paramount, I'd go ZooKeeper or etcd with fencing tokens enforced at the database layer."

---

## 7. SDE-2+ Readiness Checklist

- [ ] Can explain why OS-level mutexes don't work across processes on different machines.
- [ ] Can articulate the 3 requirements: mutual exclusion, deadlock freedom, fault tolerance.
- [ ] Can write the Redis `SET NX EX` command and explain why UUID is needed in the value.
- [ ] Can write the Lua release script for Redis and explain why it must be atomic.
- [ ] Knows that single-node Redis has an SPOF on master failover (replication gap).
- [ ] Can explain Redlock: N nodes, majority quorum, adjusted TTL.
- [ ] Can explain Martin Kleppmann's critique of Redlock (GC pauses, no fencing tokens).
- [ ] Can describe ZooKeeper ephemeral sequential nodes in detail (algorithm, thundering herd avoidance).
- [ ] Can explain etcd leases and keepalive heartbeats.
- [ ] **Can draw the GC pause scenario and explain why it invalidates distributed locks without fencing tokens.**
- [ ] Can explain fencing tokens: monotonically increasing, enforced at the resource server.
- [ ] Can identify which lock systems provide built-in fencing tokens (ZK sequential counter, etcd revision).
- [ ] Knows that Chubby is Google's internal lock service and ZooKeeper is its open-source equivalent.
- [ ] Can explain Kubernetes leader election via etcd `leaderelection` client library.
- [ ] Can discuss when Redis-based locking is "good enough" (low-stakes idempotency) vs. when ZooKeeper/etcd is required (financial transactions).
- [ ] Can describe the watch/notification mechanism in ZooKeeper and why it avoids thundering herd.
- [ ] Can explain the difference between a distributed lock and optimistic concurrency control (CAS), and when to prefer each.

---

## 8. Bonus: Distributed Lock vs. Optimistic Concurrency

**When NOT to use distributed locks:**

Distributed locks are a form of **pessimistic concurrency control** — you assume conflict and serialize access. This introduces:
- Network round-trips to acquire/release.
- A bottleneck at the lock service.
- Risk of lock-holder crashing with the lock held.

**Alternative: Optimistic Concurrency Control (OCC)**

Instead of locking, read-modify-write with a version check:
```sql
UPDATE payments
SET status = 'processed', version = version + 1
WHERE payment_id = 'pay_123'
  AND version = <version_read_earlier>
  AND status = 'pending'
```

If `rows_affected == 0`, another replica beat you to it. Retry or fail gracefully.

**Choose OCC when:** Low contention. Writes rarely conflict. You can afford retries.
**Choose distributed locks when:** High contention. Critical section is multi-step. You need to coordinate across multiple data stores atomically.

For payment processing: use OCC in the database as the primary guard, and a distributed lock for any pre-authorization steps that span multiple systems.

# ⚡ 04 - Caching Deep Dive (The SDE-2 Perspective)

## 📖 The Concept
Caching is storing copies of frequently accessed data in a fast, temporary storage location (usually in-memory) to reduce database load and improve latency.

## 📊 The SDE-2 Trade-off Table: Write Policies

| Policy | How it Works | Pros | Cons (The Trade-off) |
| :--- | :--- | :--- | :--- |
| **Write-Through** | Write to Cache AND DB simultaneously. | Data is always consistent. | Higher write latency (wait for both to finish). |
| **Write-Around** | Write directly to DB, bypass cache. | Good for data written once, rarely read. | Read misses on new data; must fetch from DB later. |
| **Write-Back** | Write to Cache ONLY. Async sync to DB. | Extremely fast writes. | **Data Loss Risk** if cache node crashes before DB sync. |

## 🚫 The Interview Trap
**"I will add Redis to speed up database queries."**
Never use a cache to fix a bad database schema. If queries are slow because you lack an index, adding a cache just hides the problem and creates a distributed consistency nightmare. 
*Better Answer:* "First, I'd ensure the DB query is optimized with correct indexing. If read throughput still exceeds DB limits, I will introduce a cache."

## 🚀 The SDE-3 Edge: Cache Stampede (Thundering Herd)
If the interviewer asks: *"An extremely popular key (like 'Superbowl_Score') expires. What happens next?"*

**The Trap:** 10,000 users ask for the key simultaneously. Cache misses. 10,000 requests slam your database instantly, crashing it.

**The SDE-3 Solution (Mutex / Probabilistic Early Expiration):**
When a cache miss occurs, the first thread acquires a **Distributed Lock (Mutex)** for that key. It queries the DB and updates the cache. The other 9,999 threads are blocked and wait, then read from the cache once the lock is released. 
Alternatively, use **Probabilistic Early Expiration** where a background thread refreshes the cache slightly *before* the actual TTL expires.

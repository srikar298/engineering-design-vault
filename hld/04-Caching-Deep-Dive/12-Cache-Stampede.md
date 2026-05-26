# Cache Stampede (C040-C053) | Category: Caching | Difficulty: 🔴 Hard | Frequency: 🔥 High

## 1. The Core Concept

### The Problem: The Thundering Herd / Cache Stampede
In high-throughput distributed systems, caching is the primary shield protecting databases and upstream services from saturation. However, when a highly requested ("hot") cache key expires, it exposes a critical vulnerability. 

If thousands of concurrent requests arrive at the exact moment of expiration, all of them will experience a cache miss simultaneously. Without coordination, every single one of these client requests will attempt to fetch or compute the data from the database.

```
                  CACHE STAMPEDE (UNMITIGATED)
                  
  Clients      Web Servers           Cache            Database
    │               │                  │                 │
    ├─(Req 1)──────>│──(Check Key)────>x [Expired]       │
    ├─(Req 2)──────>│──(Check Key)────>x                 │
    ├─(Req 3)──────>│──(Check Key)────>x                 │
    │               │                                    │
    │               ├──(Fetch 1)────────────────────────>│ [DB Saturation!]
    │               ├──(Fetch 2)────────────────────────>│ [High Latency]
    │               ├──(Fetch 3)────────────────────────>│ [Connection Pool Exh.]
    │               │                                    │
    │               |<─(Return Data 1)───────────────────┤
    │               |<─(Return Data 2)───────────────────┤
    │               |<─(Return Data 3)───────────────────┤
    │               │
    │               ├──(Write Key)────> [Cache Updated]
```

This sudden surge is called a **Cache Stampede** (or **Thundering Herd** problem). The impacts are catastrophic:
1. **Database Saturation**: CPU utilization spikes to 100%, and disk I/O saturates.
2. **Cascading Failures**: Connection pools exhaust, causing timeouts that cascade upstream, potentially taking down the entire service mesh.
3. **Redundant Computation**: The database performs the exact same expensive query thousands of times in parallel.

### The Solution: Request Collapsing & Probabilistic Expiration
To mitigate this, we must ensure that only **one execution** fetches the data from the database, while concurrent requests wait for that single fetch to complete, or we preemptively update the cache before it officially expires.

```
                  MUTEX LOCKING / REQUEST COLLAPSING
                  
  Clients      Web Servers           Cache          Singleflight        Database
    │               │                  │                 │                 │
    ├─(Req 1)──────>│──(Check Key)────>x [Expired]       │                 │
    ├─(Req 2)──────>│──(Check Key)────>x                 │                 │
    ├─(Req 3)──────>│──(Check Key)────>x                 │                 │
    │               │                                    │                 │
    │               ├──(Enter singleflight)─────────────>│                 │
    │               │  [Req 1: Acquires Lock]            │                 │
    │               │  [Req 2: Blocks & Waits]           │                 │
    │               │  [Req 3: Blocks & Waits]           │                 │
    │               │                                    │                 │
    │               ├──(Fetch 1 only)────────────────────┼────────────────>│
    │               │                                    │                 │ (Only 1 query hits DB)
    │               │<─(Return Data 1)───────────────────┼─────────────────┤
    │               │                                    │                 │
    │               ├──(Update Cache)─> [Cache Populated]│                 │
    │               │                                    │                 │
    │               ├──(Release Lock)───────────────────>│                 │
    │               │                                    │                 │
    │               │<─(Broadcast Data 1 to 1, 2, 3)─────┘                 │
    ├─(Response 1)──┤                                                      │
    ├─(Response 2)──┤                                                      │
    ├─(Response 3)──┤                                                      │
```

---

## 2. Deep Dive

### Mitigation 1: Mutex Locking / Request Collapsing (`singleflight`)
The key idea of Request Collapsing is to track active flights (pending database reads) in-memory. If a cache miss occurs for a key that is already being fetched by an active flight, subsequent requests block and wait for the original flight to complete. They then share the output.

#### Go `singleflight` Go-style Implementation
Here is a production-grade, thread-safe Go implementation of the `singleflight` pattern protecting a Redis cache.

```go
package singleflight

import (
	"context"
	"sync"
	"time"
)

// call represents an in-flight request.
type call struct {
	wg  sync.WaitGroup
	val interface{}
	err error
}

// Group represents a class of work and forms a namespace in
// which units of work can be executed with duplicate suppression.
type Group struct {
	mu sync.Mutex       // protects m
	m  map[string]*call // lazily initialized
}

// Do executes and returns the results of the given function, making
// sure that only one execution is in-flight for a given key at a time.
// If a duplicate comes in, the duplicate caller waits for the
// original to complete and receives the same results.
func (g *Group) Do(key string, fn func() (interface{}, error)) (interface{}, error) {
	g.mu.Lock()
	if g.m == nil {
		g.m = make(map[string]*call)
	}
	if c, ok := g.m[key]; ok {
		g.mu.Unlock()
		c.wg.Wait() // Block until the first call completes
		return c.val, c.err
	}
	c := new(call)
	c.wg.Add(1)
	g.m[key] = c
	g.mu.Unlock()

	g.doCall(c, key, fn)

	return c.val, c.err
}

func (g *Group) doCall(c *call, key string, fn func() (interface{}, error)) {
	defer func() {
		g.mu.Lock()
		delete(g.m, key) // Remove from map so next request triggers fresh fetch
		g.mu.Unlock()
		c.wg.Done() // Unblock waiting goroutines
	}()
	c.val, c.err = fn()
}
```

#### Integration with Cache-Aside Pattern
Here is how the application uses the `singleflight.Group` to fetch user profiles safely:

```go
package main

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	"github.com/go-redis/redis/v8"
)

type UserProfile struct {
	ID   string `json:"id"`
	Name string `json:"name"`
}

type UserService struct {
	cache       *redis.Client
	db          *Database
	flightGroup *Group // singleflight implementation
}

func (s *UserService) GetUserProfile(ctx context.Context, userID string) (*UserProfile, error) {
	cacheKey := fmt.Sprintf("user:profile:%s", userID)

	// 1. Try Cache Read
	val, err := s.cache.Get(ctx, cacheKey).Result()
	if err == nil {
		var profile UserProfile
		if err := json.Unmarshal([]byte(val), &profile); err == nil {
			return &profile, nil
		}
	}

	// 2. Cache Miss -> Execute DB Fetch via Singleflight
	data, err := s.flightGroup.Do(cacheKey, func() (interface{}, error) {
		// This inner function runs exactly once for concurrent requests on the same key
		profile, dbErr := s.db.FetchUserProfile(ctx, userID)
		if dbErr != nil {
			return nil, dbErr
		}

		// Write back to cache
		marshaled, _ := json.Marshal(profile)
		s.cache.Set(ctx, cacheKey, marshaled, 10*time.Minute)

		return profile, nil
	})

	if err != nil {
		return nil, err
	}

	return data.(*UserProfile), nil
}
```

#### Trade-offs & Limitations of Mutex/Collapsing
* **Adds Latency to Waiters**: Waiting requests will block for the full duration of the DB query. If the DB query takes 1.5 seconds, all concurrent requests on that key will hang for 1.5 seconds.
* **Single Point of Serialization**: It coordinates requests within a single process (in-memory). If you have 50 application nodes, each node will still send 1 request to the database. (Distributed locking can solve this but introduces complexity and latency).
* **Vulnerable to Thread Exhaustion**: If the database query hangs or runs very slowly, all incoming requests block, potentially exhausting application threads/goroutines. To mitigate, always configure deadlines/timeouts inside the database fetch function.

---

### Mitigation 2: Probabilistic Early Expiration (XFetch)
A highly elegant approach that does not require coordination/locking is **Probabilistic Early Expiration (XFetch)**. Published by Vattani et al., XFetch allows clients to asynchronously recalculate the cache *before* it actually expires. The probability of early recalculation increases as the key approaches its expiration time.

#### The XFetch Formula
The system decides to recompute the value if:

$$t - \beta \times \delta \times \ln(\text{rand}()) > T$$

Where:
* $t$: **Compute Time** (the time in milliseconds/seconds it takes to compute/fetch the value from the database).
* $\beta$: **Aggressiveness Constant** ($\beta > 0$). A larger $\beta$ makes early expiration more aggressive, increasing the probability of recalculation earlier in the lifespan. $\beta = 1$ is the default.
* $\delta$: **TTL** (the total lifespan/delta duration of the cached item).
* $\text{rand}()$: A random float uniformly distributed in $(0, 1]$.
* $T$: **Expiration Timestamp** (the absolute epoch timestamp when the cache key officially expires).

#### Intuition behind the Math
* As the current time $t_{\text{now}}$ (represented by the LHS variable checking mechanism) increases and approaches $T$, the difference $T - t_{\text{now}}$ shrinks.
* The term $-\beta \times \delta \times \ln(\text{rand}())$ is a random positive offset. Because $\text{rand}() \in (0, 1]$, $\ln(\text{rand}())$ is negative, making $-\ln(\text{rand}())$ positive.
* If a request comes in close to the expiration time, a small random value can trigger the condition. If a request comes in very early, the required random value to trigger expiration is statistically improbable.
* By factoring in $t$ (compute time), XFetch guarantees that expensive queries (large $t$) start their probabilistic early computation earlier in the TTL window than cheap queries (small $t$).

#### XFetch Python Implementation
Here is a production-grade implementation of XFetch:

```python
import time
import math
import random
from typing import Any, Tuple

class XCache:
    def __init__(self, cache_store: dict):
        self.store = cache_store  # In-memory mock redis

    def get(self, key: str, beta: float = 1.0) -> Tuple[Any, bool]:
        """
        Retrieves the key. Returns (value, needs_recompute).
        """
        entry = self.store.get(key)
        if not entry:
            return None, True

        value = entry["value"]
        delta = entry["ttl_delta"]  # The duration of caching (seconds)
        compute_time = entry["compute_time"]  # Time taken to compute (seconds)
        expiration = entry["expiration"]  # Epoch timestamp of hard expiry
        
        now = time.time()
        
        # XFetch check: t - beta * delta * ln(rand()) > T
        # Where t is compute_time. We want to check if the current time 'now' combined with 
        # the probabilistic factor surpasses the expiration timestamp.
        # Rearranged: now - compute_time * beta * ln(random.random()) > expiration
        rand_val = random.random()
        # Avoid math domain error with log(0)
        if rand_val == 0:
            rand_val = 0.0001
            
        probabilistic_threshold = now - (compute_time * beta * math.log(rand_val))
        
        if probabilistic_threshold > expiration:
            # Probabilistic early expiration triggered or hard expiration reached
            return value, True
            
        return value, False

    def set(self, key: str, value: Any, ttl_delta: float, compute_time: float):
        self.store[key] = {
            "value": value,
            "ttl_delta": ttl_delta,
            "compute_time": compute_time,
            "expiration": time.time() + ttl_delta
        }
```

#### Application Usage of XFetch
```python
import time

cache_store = {}
xcache = XCache(cache_store)

def fetch_heavy_analytics_from_db(user_id: str) -> dict:
    # Simulate DB latency
    time.sleep(0.4)
    return {"user_id": user_id, "score": 98.5}

def get_analytics(user_id: str) -> dict:
    key = f"user:analytics:{user_id}"
    value, needs_recompute = xcache.get(key, beta=1.0)
    
    if needs_recompute:
        # Recompute asynchronously or inline
        start_time = time.time()
        fresh_data = fetch_heavy_analytics_from_db(user_id)
        compute_duration = time.time() - start_time
        
        # Save back to cache
        xcache.set(key, fresh_data, ttl_delta=300, compute_time=compute_duration)
        return fresh_data
        
    return value
```

---

### Mitigation 3: Background Pre-heating
Instead of relying on client requests to trigger cache updates, the system asynchronously refreshes the data in the background.

```
                          BACKGROUND PRE-HEATING
                          
  Client                  App Server                 Cache               Worker/Cron
    │                         │                        │                      │
    ├─(Get User Profile)─────>│──(Read Key)───────────>│ [Hit - Fresh]        │
    │<─(Return User Profile)──┤                        │                      │
    │                         │                        │                      │
    │                         │                        │  [Cron runs every    │
    │                         │                        │   5 minutes]         │
    │                         │                        │                      │
    │                         │                        │<─(Fetch raw data)────┤
    │                         │                        │   from DB            │
    │                         │                        │──(Overwrite key)────>│
    │                         │                        │   [Updated TTL]      │
```

#### Mechanisms
1. **Dynamic Cron / Workers**: A background cron runs at a set interval (e.g., every 5 minutes) to run DB queries for hot keys and populate the cache. The TTL of the cache is set larger than the cron interval (e.g., TTL of 10 minutes, cron running every 5 minutes), guaranteeing the cache never expires while the cron is active.
2. **Event-Driven Refresh**: When data changes in the database, the system emits a Domain Event (e.g., via Kafka or RabbitMQ). A consumer listens to this event, rebuilds the cache value, and pushes it to Redis. The cache effectively becomes write-through or event-driven, rather than read-through.
3. **Soft/Hard TTLs**: The cached object contains metadata specifying a `soft_ttl` and a `hard_ttl`.
   * When `now > soft_ttl` but `now < hard_ttl`: The app returns the cached (slightly stale) data immediately to the client, and fires an asynchronous background task (e.g., via Celery, Goroutines, or Sidekiq) to fetch the fresh data from the database and refresh the cache.
   * When `now > hard_ttl`: The cache is dead; the fetch must be synchronous.

---

## 3. Comparison Table

| Feature | Simple Cache-Aside (No Mitigation) | Mutex Locking / Request Collapsing (`singleflight`) | Probabilistic Early Expiration (XFetch) | Background Pre-heating |
| :--- | :--- | :--- | :--- | :--- |
| **Read Latency Impact** | Low on hit; High on miss (DB read wait). | Low on hit; Waiting requests block, experiencing DB read latency. | Low. Never blocks requests; triggers async or early fetch. | Zero latency penalty. Reads are always cache hits. |
| **Write/DB Load Protection** | Low (allows Thundering Herd spikes). | High (collapses concurrent reads into 1 query). | High (probabilistically spreads out updates before expiry). | Extremely High (DB is queried strictly on a schedule). |
| **Implementation Complexity** | Low | Medium (requires in-memory concurrency controls). | Medium (requires recording compute time and math check). | High (requires scheduling infrastructure or worker queues). |
| **Storage Overhead** | None | Low (in-memory map tracks active flights). | Low (stores extra metadata: `compute_time`, `ttl_delta`). | High (requires persistent tracking of active hot keys). |
| **Edge Case Risks** | Cascading database failures. | Memory leak if worker hangs; thread pool exhaustion. | Over-aggressiveness leading to redundant DB writes. | Stale cache if background jobs fail or lag behind. |
| **Key Suitability** | Cold / long-tail keys. | Dynamic, medium-to-hot keys. | Highly dynamic, very hot keys. | Static, predictable, ultra-hot keys (e.g., Homepage Config). |

---

## 4. Real-world Usage

### Varnish Cache (HTTP Accelerator)
Varnish implements a feature called **Grace Mode**. When a cached HTTP response expires, Varnish can serve the stale response to incoming clients while asynchronously spawning a single thread to fetch the fresh page from the backend server. This protects the backend from a stampede of requests on dynamic landing pages.

### Redis Client Libraries (e.g., Node-cache, Redis OM)
Many enterprise Redis libraries implement wrappers that automatically serialize cache misses using local locks. For example, in Node.js, `express-redis-cache` and similar libraries coordinate cache-miss handlers through promise chaining to prevent multiple database queries for the same key.

### Netflix
Netflix handles massive throughput on hot assets (e.g., metadata for a new trending show). They employ a combination of:
* **Background Pre-heating**: Computing recommendations and hot asset manifests offline via batch jobs and pre-populating CDN edges and caches.
* **XFetch**: Implemented in edge gateways to probabilistically refresh personalized metadata caches before TTL expiration, preventing sudden traffic spikes from overwhelming downstream microservices.

---

## 5. SDE-2 Interview Script

### Scenario
The interviewer presents a scenario where a popular sports news website experiences periodic database crashes during major live events (e.g., the Super Bowl). 

### Playbook Dialogue

* **Interviewer**: "During live matches, our homepage displays real-time score updates. This data is cached in Redis with a 10-second TTL. Every 10 seconds, when the key expires, our database CPU spikes to 100%, query queues fill up, and the site becomes unresponsive for several seconds. How would you solve this?"

* **Candidate (Junior Answer)**: "I would increase the TTL from 10 seconds to 5 minutes so the database gets hit less frequently. I would also add more read replicas to the database to handle the load when the cache expires."

* **Interviewer**: "If you increase the TTL to 5 minutes, users won't see real-time scores during a live match. That's a bad user experience. Also, replicas are expensive, and if 50,000 requests hit the replicas at the same second, they will still saturate. How do we keep the scores real-time without crashing the database?"

* **Candidate (Senior Answer)**: "The core issue is a **Cache Stampede** (or Thundering Herd). When the hot key expires, thousands of concurrent requests read-miss simultaneously and query the database. Increasing the TTL or scaling replicas only kicks the can down the road.
  
  To solve this, I'd apply two architectural patterns depending on how real-time the data needs to be:
  
  First, I'd implement **Request Collapsing** at the application layer using the **Singleflight** pattern. When a cache miss occurs, instead of allowing all threads to query the database, we intercept them. The first thread acquires an in-memory lock for that key and fires the query. All other threads waiting for that key block on a lock/wait group. Once the first thread returns the result and updates Redis, it broadcasts the result to the waiting threads. This reduces database queries from 50,000 to exactly 1.
  
  Second, if we want to avoid blocking requests at all, we can implement **Probabilistic Early Expiration (XFetch)**. We write back the compute time to the cache. As the key nears expiration, incoming requests probabilistically trigger an early background fetch based on the formula $t - \beta \times \delta \times \ln(\text{rand}()) > T$. This refreshes the key in the background, ensuring clients always experience a cache hit."

* **Interviewer**: "Excellent. Tell me about the trade-offs of the Singleflight pattern. What happens if the database query hangs?"

* **Candidate (Senior Answer)**: "If the database query hangs, the thread executing the fetch is stuck. Because all other concurrent requests are blocked waiting on that same flight group, they will also hang, leading to thread/goroutine leaks and connection pool exhaustion. 
  
  To prevent this, I would enforce strict timeouts using a context timeout inside the singleflight fetch execution (e.g., 2 seconds max). I would also implement a fallback mechanism: if the database query fails or times out, the singleflight should fail fast, and we could return stale data from cache if we configure a soft TTL."

---

## 6. SDE-2+ Readiness Checklist

- [ ] I understand the root cause of a Cache Stampede (Thundering Herd) and can explain how it differs from simple high traffic.
- [ ] I can write code implementing the Singleflight / request collapsing pattern using synchronization primitives (mutexes, wait groups, promises).
- [ ] I understand the mathematical variables of the XFetch algorithm ($t, \beta, \delta, T$) and how they influence early expiration probability.
- [ ] I can explain the trade-offs between Mutex Locking, XFetch, and Background Pre-heating.
- [ ] I know how to configure soft and hard TTLs to return stale data while executing background cache refreshes.
- [ ] I am aware of the failure modes of Singleflight (e.g., thread leakage on slow queries) and how to mitigate them using deadlines and circuit breakers.
- [ ] I can evaluate when to apply background worker-driven pre-heating versus application-driven read-through cache strategies.

# Distributed Rate Limiting (C055)

* **ID**: C055
* **Category**: System Resiliency
* **Difficulty**: 🟡 Medium
* **Frequency**: 🔥 High

---

## 1. The Core Concept

### The Problem: Resource Exhaustion & Cascade Failures
In distributed architectures, services are designed with finite capacities—defined by CPU cores, thread pool sizes, database connections, and memory. Without a mechanism to control incoming traffic density, a service is susceptible to:
1. **Resource Starvation (Noisy Neighbor)**: A single misconfigured client or malicious actor sends an excessive volume of requests, consuming shared thread pools or database connections, thereby degrading performance for all other tenants.
2. **Denial of Service (DoS/DDoS)**: Intentional floods of traffic designed to overwhelm servers, driving up latency and error rates.
3. **Cascading Failures**: When a downstream dependency degrades, upstream queues fill up. Threads block waiting for responses, causing the upstream service's memory/thread limit to be reached, crashing the entire system.

### The Solution: Rate Limiting
A rate limiter acts as a control valve placed at the edge (e.g., API Gateway) or at the service boundary. It inspects request attributes (such as IP address, API key, User ID, or Tenant ID) and enforces maximum request thresholds over defined time intervals. If a client exceeds their allowance, the rate limiter intercepts the request, returning a `429 Too Many Requests` response before downstream business logic executes.

### Core Algorithms
Below is a deep look into the primary rate-limiting algorithms, their mathematical foundations, and execution profiles.

#### 1. Token Bucket
A container of fixed capacity $B$ is filled with tokens at a constant rate $r$ tokens per second. When a request arrives:
* If tokens are available in the bucket (at least 1, or $k$ for weighted requests), the tokens are deducted, and the request is allowed.
* If the bucket is empty, the request is rejected immediately or queued.

*Mathematical Formulation:*
At any request arrival time $t$, the number of available tokens $T(t)$ is calculated lazily to avoid running a background ticker thread:
$$T(t) = \min(B, T(t_{last}) + (t - t_{last}) \times r)$$
Where $t_{last}$ is the timestamp of the last processed request.

#### 2. Leaky Bucket
A bucket with a small hole at the bottom. Requests enter the bucket at arbitrary bursts, but leak out of the bottom at a constant, fixed rate $r$. The bucket has a maximum capacity $B$.
* If the incoming request rate exceeds the leak rate, the bucket fills up.
* Any request arriving when the bucket is full overflows (rejected/dropped).
* This guarantees a strictly smooth egress rate, eliminating bursts.

*Difference from Token Bucket:*
* **Token Bucket** allows bursts up to the capacity $B$ instantly (if the bucket is full, $B$ requests can go through concurrently).
* **Leaky Bucket** enforces a maximum egress rate, smoothing out bursts. Even if $B$ requests arrive simultaneously, they are processed at rate $r$.

#### 3. Sliding Window Log
Stores a timestamped log of all requests for a user in a sorted set or array. When a new request arrives at time $t$:
1. Remove all logged timestamps older than $t - W$ (where $W$ is the window size).
2. Count the remaining timestamps in the log.
3. If the count is less than the limit $L$, append the new timestamp $t$ to the log and allow the request.
4. If the count is $\ge L$, reject the request.

*Accuracy:*
* Provides $100\%$ accuracy. There are no boundary/reset reset issues.
* *Drawback:* Memory footprint is high, as every request requires storing a timestamp. Under heavy load, memory usage scales linearly $O(N)$ with the number of requests.

#### 4. Sliding Window Counter
An optimization that combines the low memory footprint of Fixed Window Counter with the accuracy of Sliding Window Log. It divides time into fixed buckets (e.g., 1 minute) and uses the current window count and previous window count to approximate the rate in a sliding window.

*Mathematical Approximation Formula:*
Let:
* $W$ be the window size (e.g., 1 minute).
* $t_{curr}$ be the current timestamp, falling in the current window bucket $B_{curr}$.
* $B_{prev}$ be the previous window bucket.
* $C_{prev}$ and $C_{curr}$ be the request counts in $B_{prev}$ and $B_{curr}$ respectively.
* $o$ be the elapsed time offset inside the current bucket (i.e., $o = t_{curr} \pmod W$).

The estimated request count in the sliding window of size $W$ is:
$$\text{Count}_{est} = C_{prev} \times \left(1 - \frac{o}{W}\right) + C_{curr}$$

*Error Bound Mathematical Derivation:*
Let the previous window contain $C_{prev}$ requests and the current window contain $C_{curr}$ requests.
The approximation assumes that requests in the previous window $B_{prev}$ are uniformly distributed over time. Let's analyze the mathematical limits of this error:

1. **Worst Case Scenario A (Burst at End of Previous Window)**:
   If all $C_{prev}$ requests occurred at the very end of $B_{prev}$ (e.g., at $t = 59.9\text{s}$ of the previous minute), the true request count in the last 60 seconds is $C_{prev} + C_{curr}$.
   The formula approximates it as:
   $$\text{Count}_{est} = C_{prev} \times \left(1 - \frac{o}{W}\right) + C_{curr}$$
   Thus, the absolute error is:
   $$\text{Error}_{abs} = C_{prev} \times \frac{o}{W}$$
   If $o = 30\text{s}$, the error is $0.5 \times C_{prev}$ requests (the rate limiter under-counts, allowing excess traffic).

2. **Worst Case Scenario B (Burst at Start of Previous Window)**:
   If all $C_{prev}$ requests occurred at the very start of $B_{prev}$ (e.g., at $t = 0.1\text{s}$ of the previous minute), the true request count in the last 60 seconds is only $C_{curr}$.
   The formula approximates it as:
   $$\text{Count}_{est} = C_{prev} \times \left(1 - \frac{o}{W}\right) + C_{curr}$$
   The absolute error is:
   $$\text{Error}_{abs} = C_{prev} \times \left(1 - \frac{o}{W}\right)$$
   Here, the rate limiter over-counts, potentially rejecting legitimate requests.

Under standard web traffic patterns (which tend to follow a Poisson arrival process rather than extreme edge bursts), the variance is smoothed out, resulting in a real-world error rate of **under 1%**.

---

### Flow Diagrams

```
[Token Bucket Flow]
   Request Arrival
         │
         ▼
 ┌───────────────┐      Add tokens lazily:
 │ Calculate     │◄──── T(t) = min(B, T(t_last) + dt * r)
 │ Token Balance │
 └───────┬───────┘
         │
         ├──────────────────────┐
         ▼ [T(t) >= 1]          ▼ [T(t) < 1]
 ┌───────────────┐      ┌───────────────┐
 │ Deduct 1      │      │ Reject / 429  │
 │ Allow Request │      │ Limit Exceeded│
 └───────────────┘      └───────────────┘

[Leaky Bucket Flow]
   Request Arrival
         │
         ▼
 ┌───────────────┐
 │ Is Bucket     ├──────────────┐
 │ Full? (Q > B) │              │ Yes
 └───────┬───────┘              ▼
         │ No           ┌───────────────┐
         ▼              │ Reject / 429  │
 ┌───────────────┐      └───────────────┘
 │ Enqueue       │
 │ Request       │
 └───────┬───────┘
         │
         ▼ (Leaking Queue at rate r)
 ┌───────────────┐
 │ Process       │
 │ Request       │
 └───────────────┘

[Sliding Window Counter Bucket Weight Visualization]
Previous Minute Counter (C_prev = 100)      Current Minute Counter (C_curr = 30)
 ┌───────────────────────────┬──────────┐ ┌──────────┬───────────────────────────┐
 │                           │ Keep 70% │ │ Keep 100%│                           │
 └───────────────────────────┴──────────┘ └──────────┴───────────────────────────┘
 0s                         42s        60s│0s        18s                         60s
 <─────── Old Window (Discarded) ───────> │ <───────── Active Window ──────────>
                                         Timestamp (Current t = 18s)
                                         Estimated Count = 100 * 0.7 + 30 = 100
```

---

## 2. Deep Dive: Distributed Implementation at Scale

### Redis Lua Script for Atomic Sliding Window Counter
When rate limiting in a distributed system with multiple API Gateway instances, a shared counter store like Redis is used. Checking the count and updating it in separate operations leads to race conditions. A Redis Lua script solves this by running atomically inside the Redis engine.

The following script implements a highly accurate, memory-efficient **Sliding Window Counter** using Redis hashes.

```lua
-- Redis Keys:
-- KEYS[1]: The rate limit key (e.g., "rl:{userid}:active")
-- KEYS[2]: The previous window rate limit key (e.g., "rl:{userid}:prev")
-- ARGV[1]: The window size in seconds (e.g., 60)
-- ARGV[2]: The current window bucket timestamp (e.g., Unix timestamp divided by window size)
-- ARGV[3]: The current microsecond offset inside the current bucket
-- ARGV[4]: The request limit (e.g., 100)

local current_key = KEYS[1]
local prev_key = KEYS[2]
local window_size = tonumber(ARGV[1])
local current_bucket = ARGV[2]
local offset_sec = tonumber(ARGV[3])
local limit = tonumber(ARGV[4])

-- Retrieve current and previous bucket values
local current_count = tonumber(redis.call('GET', current_key) or "0")
local prev_count = tonumber(redis.call('GET', prev_key) or "0")

-- Calculate sliding window weight
-- offset_ratio ranges from 0.0 to 1.0
local offset_ratio = offset_sec / window_size
local weight = 1.0 - offset_ratio

-- Calculate estimated count
local estimated_count = math.floor(prev_count * weight + current_count)

if estimated_count < limit then
    -- Increment the current counter
    redis.call('INCRBY', current_key, 1)
    
    -- Set TTL on the current key to be twice the window size to ensure it
    -- survives into the next window for calculations
    redis.call('EXPIRE', current_key, window_size * 2)
    return {1, limit - estimated_count - 1} -- Return [Allowed = True, Remaining]
else
    return {0, 0} -- Return [Allowed = False, Remaining = 0]
end
```

#### Line-by-Line Script Walkthrough
1. **`local current_key = KEYS[1]`**: Retrieves the key representing the active window block.
2. **`local prev_key = KEYS[2]`**: Retrieves the key representing the previous window block.
3. **`local current_count = tonumber(redis.call('GET', current_key) or "0")`**: Queries Redis for the number of requests already served in the current block, defaulting to `0`.
4. **`local prev_count = tonumber(redis.call('GET', prev_key) or "0")`**: Queries Redis for the number of requests served in the previous block, defaulting to `0`.
5. **`local offset_ratio = offset_sec / window_size`**: Determines how far along we are in the current window.
6. **`local weight = 1.0 - offset_ratio`**: Calculates the percentage of weight to apply to the previous window.
7. **`local estimated_count = math.floor(prev_count * weight + current_count)`**: Applies the sliding window approximation formula.
8. **`if estimated_count < limit then`**: Evaluates if the client is still within their allowed limit.
9. **`redis.call('INCRBY', current_key, 1)`**: Performs an atomic increment on the current window counter.
10. **`redis.call('EXPIRE', current_key, window_size * 2)`**: Sets a TTL of $2W$ to ensure the key is retained long enough to act as the `prev_key` during the subsequent window, but garbage collects automatically afterwards.

---

### At-Scale Concerns

#### 1. Handling Clock Drift Across API Servers
The sliding window counter depends on time offsets. If API servers calculate timestamps locally, system clock drift (due to virtualization latency, network variance, or hardware anomalies) can result in inconsistent rate limits.
* **The Vulnerability**: If Server A's clock is 2 seconds ahead of Server B's clock, they will calculate different offsets ($o$) and write to different bucket keys in Redis, potentially letting traffic exceed the strict limit or rejecting legitimate requests.
* **The Solution**: 
  1. Do not use local system clocks on API servers to calculate timestamps. Instead, use the Redis cluster's time via the Redis `TIME` command inside the Lua script.
  2. Implement Network Time Protocol (NTP) daemons (such as `chronyd`) on all API servers to keep system clocks synchronized within a few milliseconds of a central source.
  3. If using local clocks, design the rate limiter to tolerate slight drift by utilizing larger bucket sizes (e.g., 5-minute buckets) where a few milliseconds of drift represent a negligible percentage of the window.

#### 2. Race Conditions under Redis Cluster Partitioning
In a multi-master Redis cluster or during a network partition (split-brain scenario):
* **The Partition Vulnerability**: If a Redis replica is promoted to master during a network partition, or if active-active multi-region replication lag is present, the rate-limiting keys may not have synced. This leads to the "double spending" problem, where a user can consume their limit twice against different Redis instances.
* **The Solution**:
  * **Strong Consistency (Redlock/Consul)**: Enforce distributed locks or read/write majorities across multiple Redis nodes before allowing requests. *Trade-off:* This introduces massive latency overhead ($10\text{–}30\text{ms}$), which defeats the purpose of an API gateway-level rate limiter.
  * **Eventual Consistency with Local Fallbacks (Industry Standard)**: Accept that under partition, users might briefly bypass the rate limit. If Redis becomes unreachable, fail-open (allow the traffic but log warning events) or fall back to a local in-memory rate limiter using a sliding window algorithm (such as Guava RateLimiter or Token Bucket in memory) capped at a fraction of the global limit (e.g., $\text{Global Limit} / \text{Number of Gateway Nodes}$).

#### 3. Local Caching/Batching Optimization to Reduce Redis Load
A global Redis cluster can become a performance bottleneck if every incoming API request requires a synchronous round-trip to Redis. At $100,000+$ requests per second, Redis network I/O becomes expensive.

```
                  ┌───────────────────────┐
                  │      Client Request   │
                  └───────────┬───────────┘
                              │
                              ▼
               ┌─────────────────────────────┐
               │    API Gateway Instance     │
               │   (Runs Local Token Store)  │
               └──────────────┬──────────────┘
                              │
             ┌────────────────┴────────────────┐
             ▼                                 ▼
   [Local Token Available]           [Local Token Exhausted]
     Allow Immediately             Fetch batch from Redis (e.g., 50 tokens)
      (0 Redis Latency)            Write back local increments asynchronously
```

* **Token Pre-fetching & Batching**: Rather than executing a Redis call per request, the API Gateway node fetches a batch of tokens (e.g., 50 tokens) for a user key at once and stores them in a local in-memory Token Bucket. 
* **Asynchronous Write-Back**: The local node increments a local counter and flushes the accumulated requests to Redis in batches (e.g., every 100ms or when the local batch is exhausted) using pipeline commands.
* **Performance Impact**: Reduces Redis network round-trips by $90\text{--}98\%$, cutting API gateway tail latencies down to sub-millisecond ranges for rate limit validation.

---

### Gateway Middleware Execution Logic (Pseudo-code)
The following structured logic executes inside the API Gateway's request pipeline for every inbound HTTP call:

```python
import time
import redis

class RateLimiterMiddleware:
    def __init__(self, redis_client, limit=100, window_size=60):
        self.redis = redis_client
        self.limit = limit
        self.window_size = window_size
        # Local Token Cache structure: { tenant_id: {"tokens": count, "expires": timestamp} }
        self.local_token_cache = {}

    def handle_request(self, request):
        tenant_id = request.headers.get("X-Tenant-ID")
        if not tenant_id:
            return HTTPResponse(status=400, body="Missing Tenant ID")
        
        # Check Local Token Cache first
        now = time.time()
        if tenant_id in self.local_token_cache:
            cache_entry = self.local_token_cache[tenant_id]
            if cache_entry["tokens"] > 0 and cache_entry["expires"] > now:
                cache_entry["tokens"] -= 1
                return self.allow_and_forward(request, remaining=cache_entry["tokens"])
        
        # Fallback to central Redis check using atomic Sliding Window Counter
        try:
            current_bucket = int(now // self.window_size)
            offset_sec = now % self.window_size
            
            current_key = f"rl:{tenant_id}:{current_bucket}"
            prev_key = f"rl:{tenant_id}:{current_bucket - 1}"
            
            # Execute atomic evaluation Lua script
            allowed, remaining = self.execute_lua_script(
                keys=[current_key, prev_key],
                args=[self.window_size, current_bucket, offset_sec, self.limit]
            )
            
            if allowed:
                # Top up local token cache for optimization
                self.local_token_cache[tenant_id] = {
                    "tokens": int(remaining * 0.1), # Fetch 10% of remaining tokens locally
                    "expires": now + (self.window_size - offset_sec)
                }
                return self.allow_and_forward(request, remaining)
            else:
                return self.reject_request(retry_after=int(self.window_size - offset_sec))
                
        except redis.RedisError as e:
            # Resiliency fallback: Fail open under Redis downtime
            print(f"Log Warning: Redis rate limiter failed: {e}. Failing open.")
            return self.allow_and_forward(request, remaining=-1)

    def allow_and_forward(self, request, remaining):
        response = forward_to_service(request)
        response.headers["X-RateLimit-Limit"] = str(self.limit)
        response.headers["X-RateLimit-Remaining"] = str(remaining if remaining >= 0 else "unlimited")
        return response

    def reject_request(self, retry_after):
        response = HTTPResponse(status=429, body="Rate Limit Exceeded")
        response.headers["Retry-After"] = str(retry_after)
        return response
```

---

## 3. Comparison of Algorithms

| Feature / Metric | Token Bucket | Leaky Bucket | Sliding Window Log | Sliding Window Counter |
| :--- | :--- | :--- | :--- | :--- |
| **Memory Footprint** | $O(1)$ (stores count + timestamp) | $O(1)$ (stores queue size + timestamp) | $O(N)$ (scales linearly with request count) | $O(1)$ (stores two integer counters) |
| **Time Complexity** | $O(1)$ | $O(1)$ | $O(\log N)$ or $O(N)$ (clearing outdated items) | $O(1)$ |
| **Burst Support** | Yes (up to bucket capacity $B$) | No (egress is strictly smoothed) | Yes (up to window limit $L$) | Yes (up to window limit $L$) |
| **Accuracy** | High | High | Perfect ($100\%$ accurate) | High (Approximated, $< 1\%$ error rate) |
| **Implementation Complexity** | Medium (requires lazy calculation logic) | High (requires a queue and background worker) | Low | Medium-High (requires multi-key calculations) |
| **Primary Use Cases** | API Gateways, Tenant-level burst control | Traffic shaping, Egress packet queues (routers) | Low-volume, high-value security endpoints | Scalable distributed API Gateways |

---

## 4. Real-World Usage

### 1. Stripe API Rate Limiting
Stripe uses the **Token Bucket** algorithm to limit incoming API requests. The rate limiting is done at the scale of user API keys.
* **Mechanism**: They utilize Redis to maintain the token bucket counts.
* **Headers**: Stripe provides custom response headers indicating limit status:
  * `X-RateLimit-Limit`: The total request quota allowed per window.
  * `X-RateLimit-Remaining`: The remaining quota in the current window.
  * `X-RateLimit-Reset`: The Unix epoch timestamp indicating when the current window expires and resets.

### 2. GitHub API
GitHub employs a **Fixed/Sliding Window** rate-limiting mechanism.
* **Behavior**: Authenticated requests have higher limits (e.g., 5,000 requests per hour) than unauthenticated requests (60 requests per hour).
* **Headers**:
  * `x-ratelimit-limit`: `5000`
  * `x-ratelimit-remaining`: `4999`
  * `x-ratelimit-reset`: `1372700873` (epoch seconds)
* When a client is rate limited, the API returns a `429` status code along with a `Retry-After` header indicating the number of seconds the client must sleep before retrying.

### 3. AWS API Gateway
AWS uses a **Token Bucket** algorithm internally to govern request execution per account per region.
* **Configuration**: AWS allows users to define both a steady-state rate (tokens added per second) and a burst limit (total bucket capacity).
* **Behavior**: If the rate limit is exceeded, it returns a `429 Too Many Requests` response.

---

## 5. SDE-2 Interview Script

### Scenario
The interviewer asks: *"Design a rate limiting system for a high-traffic SaaS API gateway. It needs to support millions of active tenants, enforce limits globally across multiple regions, handle sudden bursts gracefully, and introduce minimal latency overhead."*

#### Step 1: Requirements Gathering & Clarifications
* **Candidate**: "Before diving into the design, I'd like to clarify a few parameters. What is the scale we are looking at? What are the typical rate-limit windows (e.g., requests per second, minute, or hour)? Also, is accuracy a hard requirement, or is minor estimation acceptable in exchange for performance?"
* **Interviewer**: "We handle 500,000 requests per second globally. The limits are typically defined per tenant (e.g., 10,000 requests per minute). Accuracy needs to be high, but we can tolerate minor estimation errors ($< 2\%$) under peak load if it prevents latency degradation."
* **Candidate**: "Understood. A high throughput of 500k RPS rules out the Sliding Window Log due to memory overhead, which would be $O(N)$ write-heavy. I will choose the **Sliding Window Counter** algorithm implemented over Redis. This gives us $O(1)$ memory usage and low error bounds. To minimize latency overhead at the API Gateway layer, I will implement **Local Token Batching** to reduce direct Redis queries."

#### Step 2: High-Level Architecture & Algorithm Choice
* **Candidate**: "Here is how the request flow works. The client makes a call to our API Gateway. The Gateway extracts the Tenant ID. To check the rate limit:
  1. It first checks its local in-memory token cache for that Tenant ID.
  2. If it has cached tokens, it decrements one locally and forwards the request immediately. This reduces network hops and keeps response latency sub-millisecond.
  3. If the local cache is empty, it makes a call to Redis using an atomic Lua script to fetch a batch of tokens (e.g., 100 tokens).
  4. The Lua script evaluates the sliding window count using the current and previous window buckets to ensure accuracy."

```
                       ┌─────────────────────────┐
                       │      Client Request     │
                       └────────────┬────────────┘
                                    │
                                    ▼
                       ┌─────────────────────────┐
                       │       API Gateway       │
                       │ ┌─────────────────────┐ │
                       │ │ Local Token Cache   │ │
                       │ └──────────┬──────────┘ │
                       └────────────┼────────────┘
                                    │
                       ┌────────────┴────────────┐
             [Cache Hit]                         [Cache Miss]
                   │                                   │
                   ▼                                   ▼
          Forward to Microservice            Execute Redis Lua Script
                                             (Atomic Sliding Window)
```

#### Step 3: Deep Dive into the Lua Script & Synchronization
* **Interviewer**: "How does your Redis Lua script calculate the rate limit atomically?"
* **Candidate**: "The script uses two Redis keys per user: `user:{id}:current` and `user:{id}:prev`. When a request occurs, we pass the current time offset inside the window. The script computes the weight of the previous window bucket as `1.0 - (offset / window_size)`. It estimates the count as `(prev_count * weight) + current_count`. If the estimate is below the threshold, it increments the current bucket and returns success. This guarantees that checking and incrementing happen atomically without race conditions."
* **Interviewer**: "What if the clocks on the API Gateway instances drift? Won't they calculate different time offsets?"
* **Candidate**: "Yes, client clock drift is a real issue. To prevent this, the Lua script will query the Redis server's time directly using `redis.call('TIME')`. This returns a highly accurate timestamp from the Redis server itself, meaning all API Gateways sync to the same clock source. Additionally, we enforce NTP on all our gateway hosts to keep drift under $5\text{ms}$."

#### Step 4: Resiliency, Partitioning, and Edge Cases
* **Interviewer**: "What happens if the Redis cluster becomes unavailable or partitioned due to network failure? Does our API Gateway stop accepting requests?"
* **Candidate**: "No, we must fail-open to preserve system availability. If Redis is unreachable or times out (which we will configure with a strict $15\text{ms}$ circuit breaker timeout), the API Gateway falls back to local in-memory rate limiting. It will limit traffic using a local, thread-safe Token Bucket structure capped at a conservative fraction of the user's limit. We also log a critical alert so the operations team can inspect the Redis cluster."
* **Interviewer**: "Excellent. What response headers would you return to help clients behave politely?"
* **Candidate**: "I will return standard rate-limiting headers: `X-RateLimit-Limit`, `X-RateLimit-Remaining` (derived from our estimated remaining limit returned by the Lua script), and `X-RateLimit-Reset` showing when the current window closes. If they hit the limit, we return `429 Too Many Requests` with a `Retry-After` header so their client SDK knows exactly how long to back off before trying again."

---

## 6. SDE-2+ Readiness Checklist

- [ ] **Algorithm Trade-offs**: Understand when to choose Token Bucket (supports bursts) vs. Leaky Bucket (enforces uniform flow) vs. Sliding Window Counter (memory efficient and accurate).
- [ ] **Atomic Execution**: Know how to write and optimize Redis Lua scripts to eliminate check-then-act race conditions.
- [ ] **Clock Synchronization**: Explain the impact of clock drift on time-based rate limiters and how to mitigate it (e.g., using Redis `TIME` or NTP).
- [ ] **Local Cache Batching**: Explain how to reduce Redis load by caching/pre-fetching tokens locally in gateway memory.
- [ ] **Fail-Open vs. Fail-Closed**: Design fallback strategies (e.g., local rate limiting or open bypass) when the centralized database/cache goes down.
- [ ] **HTTP Specifications**: Standardize rate limit headers (`X-RateLimit-*`, `Retry-After`) and return the appropriate HTTP `429` status code.
- [ ] **Key Design**: Formulate optimal Redis key schemas (e.g., `rate:{tenant_id}:{endpoint_hash}:{bucket_timestamp}`) to prevent key hotspots in Redis clusters.
- [ ] **Distributed Partition Handling**: Understand the implications of CAP theorem on Redis rate-limiting (choosing high availability over strict consistency during split-brain).

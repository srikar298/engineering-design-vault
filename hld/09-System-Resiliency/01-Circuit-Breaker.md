# Distributed Circuit Breakers (C090-C095)

* **ID**: C090-C095
* **Category**: System Resiliency
* **Difficulty**: 🟡 Medium
* **Frequency**: 🔥 High

---

## 1. The Core Concept

### The Problem: Cascading Failures & Retry Storms
In microservice architectures, services make remote calls to downstream dependencies over network boundaries. If a downstream service slows down (e.g., due to database lock contention, memory leaks, or bad deployment) or fails completely:
1. **Thread Pool Exhaustion**: Upstream services continue sending requests. Threads block waiting for responses, holding onto memory, socket connections, and execution contexts. Soon, the upstream service's thread pool is completely depleted, rendering it unable to handle calls to other healthy services.
2. **Latency Amplification**: Latency cascades upstream, slowing down the entire system.
3. **Retry Storms**: When requests fail, upstream services often retry immediately or with poor backoff algorithms, further flooding the degraded downstream service and preventing it from recovering.

### The Solution: The Circuit Breaker Pattern
A Circuit Breaker acts as an inline wrapper around remote calls. It monitors success, failure, and execution duration. 
* Under normal conditions, the circuit is **Closed**, and calls pass through to the dependency.
* If failures or timeouts exceed a defined threshold, the circuit trips to the **Open** state. While Open, all calls fail fast immediately, bypass the network, and return a fallback response. This stops downstream load and prevents upstream thread pool starvation.
* After a cooldown period (sleep window), the circuit moves to the **Half-Open** state. A limited number of canary calls are allowed through. If they succeed, the circuit returns to the **Closed** state (normal operation). If they fail, it returns to the **Open** state.

---

### State Machine Transition Diagram

```
                       ┌────────────────────────┐
                       │                        │
                       │         CLOSED         │◄──────────────────────────┐
                       │  (Normal Operation)    │                           │
                       │                        │                           │
                       └───────────┬────────────┘                           │
                                   │                                        │
                                   │ Trip Triggered:                        │
                                   │ Error Rate > Threshold %               │ Canary calls
                                   │ OR Slow Call % > Threshold             │ succeed
                                   │ (Min throughput met)                   │
                                   ▼                                        │
                       ┌────────────────────────┐                           │
                       │                        │                           │
                       │          OPEN          │                           │
                       │     (Fail Fast /       │                           │
                       │    Fallback Active)    │                           │
                       │                        │                           │
                       └───────────┬────────────┘                           │
                                   │                                        │
                                   │ Sleep Window                           │
                                   │ Expires                                │
                                   │                                        │
                                   ▼                                        │
                       ┌────────────────────────┐                           │
                       │                        │                           │
                       │       HALF-OPEN        ├───────────────────────────┘
                       │   (Canary Monitoring:  │
                       │    Limited Traffic)    ├───────────────────────────┐
                       │                        │                           │
                       └────────────────────────┘                           │
                                                                            │ Canary call
                                                                            │ fails
                                                                            ▼
                                                                  [Return to OPEN State]
```

---

## 2. Deep Dive: State Engine & Internal Mechanics

### State Transition Engine Criteria
To prevent premature tripping or delayed detection, modern circuit breakers (like Resilience4j) use complex math-based triggers.

```
       Closed (Success/Failure Metrics Collected in Sliding Window)
                               │
               [Calculate Error/Slow Percentage]
                               │
                               ▼
        Is minimum throughput met? (e.g., minimum 20 calls)
             ├── No ──> Keep Closed (Insufficient data)
             └── Yes
                  ▼
         Does Error Rate > Threshold? (e.g., > 50%)
         OR Does Slow Call Rate > Threshold? (e.g., > 75%)
             ├── No ──> Keep Closed
             └── Yes ──> Transition to OPEN
```

1. **Failure Rate Threshold**: The percentage of calls that failed (returned exceptions or system errors) compared to total calls. Typically set between $50\%$ and $80\%$.
2. **Slow Call Threshold**: The percentage of calls that completed successfully but took longer than a specified latency threshold (e.g., $2000\text{ms}$). Tripping on slow calls protects the system against performance degradation.
3. **Minimum Throughput Threshold (Minimum Number of Calls)**: Prevents the circuit breaker from tripping on statistical anomalies. For instance, if only 2 requests are sent, and 1 fails, the error rate is $50\%$. If the threshold is $50\%$ but the minimum throughput is set to 20, the circuit will **not** trip because 2 requests are insufficient statistical data.
4. **Sleep Window Duration**: The amount of time the circuit breaker remains in the **Open** state before automatically transitioning to **Half-Open** to test the downstream dependency's health.

---

### Sliding Window Implementations

Circuit breakers must record statistics (successes, failures, and execution durations) over a rolling window. There are two primary ways to structure this window:

#### A. Count-Based Sliding Window
Measures metrics over a fixed number of recent requests (e.g., the last 100 requests).
* **Implementation**: Uses a circular queue or ring buffer.
* **Operation**: When a request completes, the result is pushed into the buffer. The oldest result is evicted. The error rate is calculated by scanning the buffer.
* **Memory**: Fixed memory footprint: $O(W)$, where $W$ is the window size.

#### B. Time-Based Sliding Window
Measures metrics over a fixed time period (e.g., the last 10 seconds).
* **Implementation**: A naive implementation stores timestamped events and removes elements older than $t - W$. This is CPU and memory-intensive.
* **Optimization (Bucketed Sliding Window)**: Time is divided into $N$ buckets (e.g., a 10-second window divided into 10 buckets of 1 second each). Each bucket keeps aggregated statistics (success, failure, slow count).

```
   Bucket 1    Bucket 2    Bucket 3    Bucket 4    Bucket 5    Bucket 6 ...
 ┌──────────┐┌──────────┐┌──────────┐┌──────────┐┌──────────┐┌──────────┐
 │ S:12,F:2 ││ S:15,F:1 ││ S:10,F:0 ││ S:8, F:3 ││ S:14,F:0 ││ S:20,F:1 │
 └──────────┘└──────────┘└──────────┘└──────────┘└──────────┘└──────────┘
 [1s - 2s]   [2s - 3s]   [3s - 4s]   [4s - 5s]   [5s - 6s]   [6s - 7s]
                                                             ▲
                                                        Current Time
```

* When time rolls forward by 1 second, a new bucket is initialized at the head of the circular array, and the oldest bucket is reset.
* To check the status, the circuit breaker sums the statistics across all active buckets in the circular array.
* **Performance**: This reduces the space complexity to $O(B)$ (where $B$ is the fixed number of buckets, regardless of the request rate) and time complexity to $O(B)$ lookup.

---

### In-Memory Sliding Window Implementation (Python Example)
Below is a thread-safe implementation of a bucketed sliding window circuit breaker to demonstrate how metrics are aggregated in time-slice buckets.

```python
import time
import threading

class Bucket:
    def __init__(self):
        self.successes = 0
        self.failures = 0
        self.slow_calls = 0
        
    def reset(self):
        self.successes = 0
        self.failures = 0
        self.slow_calls = 0

class BucketedSlidingWindowCircuitBreaker:
    def __init__(self, window_size_seconds=10, num_buckets=10, 
                 failure_rate_threshold=50.0, min_calls=10, slow_call_threshold_ms=1000):
        self.window_size = window_size_seconds
        self.num_buckets = num_buckets
        self.bucket_width = window_size_seconds / num_buckets
        self.failure_threshold = failure_rate_threshold
        self.min_calls = min_calls
        self.slow_call_threshold_ms = slow_call_threshold_ms
        
        self.buckets = [Bucket() for _ in range(num_buckets)]
        self.bucket_timestamps = [0.0] * num_buckets
        self.lock = threading.Lock()
        
        # States: "CLOSED", "OPEN", "HALF-OPEN"
        self.state = "CLOSED"
        self.last_state_change = time.time()
        self.sleep_window = 15.0 # Cooldown window in seconds
        
    def _get_current_bucket(self, now):
        bucket_index = int((now / self.bucket_width) % self.num_buckets)
        bucket_start_time = int(now / self.bucket_width) * self.bucket_width
        
        # If the bucket is older than the current window, reset it
        if now - self.bucket_timestamps[bucket_index] >= self.window_size:
            self.buckets[bucket_index].reset()
            self.bucket_timestamps[bucket_index] = bucket_start_time
            
        return self.buckets[bucket_index]

    def record_call(self, is_success, duration_ms):
        with self.lock:
            now = time.time()
            self._update_state_if_needed(now)
            
            if self.state == "OPEN":
                return # Fast fail, calls aren't registered during open state
                
            bucket = self._get_current_bucket(now)
            
            if is_success:
                bucket.successes += 1
                if duration_ms > self.slow_call_threshold_ms:
                    bucket.slow_calls += 1
            else:
                bucket.failures += 1
                
            self._evaluate_metrics(now)

    def _evaluate_metrics(self, now):
        total_success = 0
        total_failure = 0
        total_slow = 0
        
        # Aggregate statistics from all valid buckets
        for i in range(self.num_buckets):
            if now - self.bucket_timestamps[i] < self.window_size:
                total_success += self.buckets[i].successes
                total_failure += self.buckets[i].failures
                total_slow += self.buckets[i].slow_calls
                
        total_calls = total_success + total_failure
        if total_calls < self.min_calls:
            return # Insufficient calls to make statistical decision
            
        failure_rate = (total_failure / total_calls) * 100.0
        
        if self.state == "CLOSED" and failure_rate >= self.failure_threshold:
            self._transition_to("OPEN", now)
        elif self.state == "HALF-OPEN":
            # In Half-Open, immediately evaluate canary calls
            if failure_rate >= self.failure_threshold:
                self._transition_to("OPEN", now)
            elif total_calls >= self.min_calls:
                self._transition_to("CLOSED", now)

    def _update_state_if_needed(self, now):
        if self.state == "OPEN" and (now - self.last_state_change >= self.sleep_window):
            self._transition_to("HALF-OPEN", now)

    def _transition_to(self, new_state, now):
        print(f"[State Transition] {self.state} -> {new_state} at timestamp {now}")
        self.state = new_state
        self.last_state_change = now
        
        # Clear metrics upon state change to start fresh
        for bucket in self.buckets:
            bucket.reset()

    def allow_execution(self):
        with self.lock:
            now = time.time()
            self._update_state_if_needed(now)
            if self.state == "OPEN":
                return False
            return True
```

---

### Half-Open Routing Policies & Canary Control
When a circuit breaker enters the **Half-Open** state, it operates under a strict canary routing policy:
1. **Canary Limit**: It limits concurrent requests by using a Semaphore or Atomic Counter. For example, only 10 requests are allowed to bypass the block. Any additional requests during this testing phase are rejected immediately.
2. **Evaluation Metrics**: Unlike the Closed state, which evaluates metrics over a large window, the Half-Open state uses a small, dedicated evaluation buffer (e.g., 10 calls).
3. **State Resolution**:
   * If the failure rate of the canary requests is **below** the threshold (e.g., all 10 calls succeed), the circuit transitions back to **Closed**.
   * If even a single canary call fails (or if the failure rate exceeds the threshold), the circuit transitions back to **Open**, resetting the sleep window.

---

### Thread Isolation vs. Semaphore Isolation
To prevent resource exhaustion under failure, circuit breakers isolate remote execution environments.

```
[Thread Pool Isolation (Hystrix Style)]
               API Gateway / Caller Thread
                            │
              (Hands off task to dedicated queue)
                            ▼
              ┌───────────────────────────┐
              │ Thread Pool (Downstream)  │  <-- Blocks here under failure,
              │ [Thread 1]  [Thread 2]    │      leaving caller thread free.
              └─────────────┬─────────────┘
                            │
                            ▼
                   Remote Dependency

[Semaphore Isolation (Resilience4j Style)]
               API Gateway / Caller Thread
                            │
             (Acquires permit from Semaphore)
                            ▼
                    Remote Dependency        <-- Runs execution on caller thread.
                                                 Blocks calling thread directly.
```

#### Thread Isolation (Bulkhead Pattern)
Each external dependency is assigned its own dedicated thread pool and task queue.
* **Behavior**: The caller thread submits a task to the dependency's thread pool and immediately waits or performs other work. If the downstream dependency slows down, only its dedicated thread pool fills up. The main caller threads are unaffected.
* **Advantages**: Full isolation; supports hard execution timeouts (the calling thread can interrupt the worker thread if it exceeds a threshold).
* **Disadvantages**: Heavy overhead due to CPU context switching, thread scheduling, and memory allocation.

#### Semaphore Isolation
Uses a simple concurrent counter (Semaphore) to limit the number of concurrent calls to a dependency.
* **Behavior**: Before making a call, the caller thread attempts to acquire a permit from the semaphore. If no permits are available, the call is rejected immediately. If available, the caller thread executes the call itself.
* **Advantages**: Highly efficient; near-zero memory and CPU overhead since it doesn't spin up threads or perform context switching.
* **Disadvantages**: The calling thread is blocked during execution. It cannot enforce a hard client-side timeout unless the underlying HTTP client library supports network read timeouts.

---

## 3. Comparison of Architectures and Window Techniques

### Thread Isolation vs. Semaphore Isolation

| Evaluation Metric | Thread Isolation | Semaphore Isolation |
| :--- | :--- | :--- |
| **Execution Thread** | Separate worker thread pool | Calling thread |
| **Timeout Enforcement** | Yes (can interrupt thread execution) | No (must rely on client library timeouts) |
| **CPU Context Switching** | High overhead | Low overhead |
| **Memory Footprint** | High (allocated stack memory per thread) | Extremely low (single integer counter) |
| **Best Used For** | High-latency remote calls, untrusted 3rd party APIs | High-throughput, low-latency microservices |

### Time-Based vs. Count-Based Sliding Windows

| Metric | Time-Based Sliding Window | Count-Based Sliding Window |
| :--- | :--- | :--- |
| **Evaluation Unit** | Elapsed seconds (e.g., last 10 seconds) | Total request count (e.g., last 100 requests) |
| **Memory Footprint** | Constant $O(B)$ (using $B$ time buckets) | Constant $O(W)$ (using a ring buffer of size $W$) |
| **Tripping Behavior** | Trips quickly during high-volume bursts | Trips after a fixed number of failures, regardless of time |
| **Best Used For** | Highly dynamic traffic volumes | Predictable, steady-state request volumes |

---

## 4. Real-World Usage

### 1. Netflix Hystrix (Legacy / Historical Standard)
Netflix pioneered the distributed circuit breaker pattern with **Hystrix** to defend against cascading failures across its streaming infrastructure.
* **Isolation Model**: Hystrix relied heavily on **Thread Isolation** to run dependency calls on separate threads, mitigating network timeout limitations.
* **Deprecation Note**: Hystrix was put into maintenance mode in 2018. The industry has shifted toward Resilience4j and service mesh architectures because thread context-switching became a bottleneck under high container density (Kubernetes pod scaling).

### 2. Resilience4j (Modern Java Standard)
Resilience4j is a lightweight fault tolerance library designed for functional programming in Java.
* **Isolation Model**: It favors **Semaphore Isolation** by default, using AtomicRegisters and Ring Buffers to maintain sliding windows with minimal memory footprint.
* **Spring Integration**: Integrates directly with Spring Cloud Gateway and WebFlux to protect reactive microservices.

### 3. Envoy Service Mesh (Istio Envoy Filters)
In modern cloud-native infrastructures, circuit breaking is offloaded from application code to sidecar proxies (Envoy).
* **Proxy-Level Breakers**: Envoy configures circuit breakers at the network layer, managing maximum connections, pending requests, and consecutive $5\text{xx}$ errors.
* **Advantages**: Language agnostic. Developers do not need to install libraries like Resilience4j in Go, Rust, or Node.js; the Envoy sidecar intercepts and breaks the circuit automatically.

---

## 5. SDE-2 Interview Script

### Scenario
The interviewer asks: *"You have a billing service that calls a third-party credit card gateway. Sometimes the gateway experiences high latency ($> 10\text{ seconds}$) or downtime, which crashes the billing service due to out-of-memory errors. How do you design a resilient architecture to protect the billing service?"*

#### Step 1: Diagnosing the Failure Mode
* **Candidate**: "First, I'll diagnose the root cause of the crash. The billing service is crashing because its HTTP client threads are waiting for the third-party gateway to respond. Because there are no client timeouts or concurrency limits, thread pools are filling up, holding memory, and causing an Out of Memory (OOM) error.
* To resolve this, I will implement a **Circuit Breaker** around the payment gateway client, combined with a **Thread Pool Bulkhead** for isolation."

#### Step 2: Selecting the Isolation Strategy
* **Interviewer**: "Why choose Thread Isolation (Bulkhead) over Semaphore Isolation here?"
* **Candidate**: "Since we are dealing with a third-party network call that is prone to hanging for $10\text{ seconds}$ or more, we cannot rely on Semaphore Isolation alone. With Semaphore Isolation, the calling application threads would be blocked waiting for the network read, which still risk starving our upstream HTTP listeners.
* Thread Isolation allows us to run these payment calls on a separate thread pool. If the payment gateway hangs, the dedicated pool fills up, but the billing service's main request-handling threads return a fast timeout error immediately, keeping the service responsive."

```
                 ┌────────────────────────────────────┐
                 │       Billing Service Core         │
                 └─────────────────┬──────────────────┘
                                   │
                    (Dispatches call to thread pool)
                                   ▼
                 ┌────────────────────────────────────┐
                 │     Dedicated Payment Thread Pool  │
                 │   [T1]  [T2]  [T3]                 │ <-- If this fills, calls
                 └─────────────────┬──────────────────┘     fail fast instantly.
                                   │
                                   ▼
                 ┌────────────────────────────────────┐
                 │    Third-Party Payment Gateway     │
                 └────────────────────────────────────┘
```

#### Step 3: Configuring the State Machine and Metrics
* **Interviewer**: "How would you configure the thresholds and sliding window for this circuit breaker?"
* **Candidate**: "I will use a **Time-Based Sliding Window** of 10 seconds, divided into 10 buckets of 1 second each. We will configure:
  1. A **Failure Rate Threshold** of $50\%$.
  2. A **Slow Call Threshold** of $50\%$, where any call taking longer than $2000\text{ms}$ is marked as slow.
  3. A **Minimum Throughput** of 20 requests per window to prevent premature tripping during low traffic.
  4. A **Sleep Window** of 30 seconds before transitioning to Half-Open.
* If 10 out of 20 requests fail or take longer than 2 seconds within a rolling 10-second period, the circuit trips to Open."

#### Step 4: Half-Open Canaries and Fallbacks
* **Interviewer**: "What happens when the 30-second sleep window expires?"
* **Candidate**: "The circuit transitions to **Half-Open**. We will allow a maximum of 5 concurrent canary requests through. The remaining incoming requests will continue to fail fast. 
* If all 5 canary requests succeed, the circuit returns to **Closed**. If any of them fail, we assume the dependency is still degraded, and the circuit transitions back to **Open** for another 30 seconds."
* **Interviewer**: "What does the user see when the circuit is Open?"
* **Candidate**: "We must provide a graceful **Fallback**. Rather than returning a generic server error, we can queue the transaction in a durable message broker (like RabbitMQ) for asynchronous retry, or return a user-friendly response indicating that the transaction is being processed and will be updated shortly."

---

## 6. SDE-2+ Readiness Checklist

- [ ] **State Transitions**: Understand and explain the exact conditions for transition between Closed, Open, and Half-Open states.
- [ ] **Thread vs. Semaphore Isolation**: Evaluate when to use thread-based isolation (supports timeout interruption) vs. semaphore isolation (minimal latency overhead).
- [ ] **Sliding Window Storage**: Describe the memory structure of a sliding window (e.g., circular arrays of time buckets vs. ring buffers).
- [ ] **Throughput Protection**: Use minimum throughput limits to avoid tripping the circuit breaker prematurely on statistical noise.
- [ ] **Canary Routing**: Detail the routing logic in the Half-Open state, including concurrency limits and evaluation rules.
- [ ] **Fallback Strategy**: Design resilient fallback paths (e.g., read-through local caches, write-through queues, default mock states).
- [ ] **Service Mesh Offloading**: Compare application-level libraries (Resilience4j) with infrastructure-level proxy filters (Envoy/Istio).
- [ ] **Monitoring and Metrics**: Set up dashboards tracking state transitions, execution latency, failure rates, and thread pool saturation metrics.

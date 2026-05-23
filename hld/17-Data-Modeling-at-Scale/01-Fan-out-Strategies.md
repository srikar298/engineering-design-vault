# ⚡ 01 - Fan-out Strategies

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C127 |
| **Category** | Data Modeling |
| **Difficulty** | 🟡 Medium |
| **Interview Frequency** | 🔥 High |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Fan-out strategies define whether system updates, such as social feed posts or notifications, are disseminated to subscribers at the time of writing (push model) or assembled dynamically at the time of reading (pull model). Architects trigger these strategies when designing systems that feature extreme read/write asymmetries and highly skewed follower distributions, such as social networks, message routing topologies, and notification delivery systems.
*   **Scalability Dimension:** Primary: **Read Latency** vs. **Write Throughput**. Secondary: **Memory Utilization** (Caching) & **Network Egress Bandwidth**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Core Mechanisms & Feed Architectures

When a publisher posts an update, the system must deliver this content to all subscribers. There are two primary paradigms and a hybrid solution.

#### 1. Fan-out on Write (Push Model)
*   **Mechanism:** When User A publishes a post, the application handles it by querying the database for User A's followers. It then pushes the post ID (or full metadata payload) directly into the home timelines (inboxes) of all followers. Timelines are typically stored in memory using structures like **Redis Sorted Sets (ZSets)** where the score is the post timestamp.
*   **The Read Path:** Simple and fast. Since timelines are pre-assembled, reading is $O(1)$—retrieving the top $K$ items from a single Redis key.
*   **The Write Path:** Expensive. If User A has $N$ followers, a single write triggers $N$ updates. If $N$ is large (e.g., a celebrity with 80 million followers), a single write spawns a write storm, consuming significant memory, CPU, and database IOPS.

#### 2. Fan-out on Read (Pull Model)
*   **Mechanism:** When User A publishes a post, it is written only to their personal feed (user timeline) in the database. No distribution occurs at write time.
*   **The Read Path:** Complex. When a follower logs in, the system queries the list of all users they follow (e.g., $F$ users). It then queries the most recent posts of all $F$ users from the database, merges them, and sorts them chronologically in memory.
*   **The Write Path:** Fast and simple. Writing a post is an $O(1)$ database insertion.
*   **Bottlenecks:** For users following thousands of people, merging $F$ data streams at runtime creates high CPU overhead, heavy database read pressure, and significant latency.

#### 3. The Hybrid Model (Industry Standard)
To solve the celebrity problem, systems implement a threshold-based hybrid strategy:
*   **Standard Users (Followers < $T$):** Use **Fan-out on Write**. Their updates are pushed directly to their followers' cached feeds.
*   **Celebrities / High-Follower Users (Followers $\ge T$):** Use **Fan-out on Read**. Their updates are written only to their personal stream.
*   **Feed Assembly (Read Time):** When a user requests their home feed, the system fetches their pre-computed feed (containing all standard user updates) and dynamically merges it with the recent posts of any celebrities they follow.

```
[Write Path - Standard User]
 User A (Standard) ──► Publish ──► Save Post ──► Push to Followers' Redis Feeds (ZSets)

[Write Path - Celebrity]
 User B (Celebrity) ──► Publish ──► Save Post (Celebrity's Own Feed Only)

[Read Path - Follower]
                   ┌──► Fetch Pre-computed Feed (Standard Users) from Redis
 Follower ──► Read ┼──► Fetch Celebrities Followed from DB/Cache
                   └──► Merge & Sort Chronologically in Memory (Heap Merge)
```

### Comparison: Push vs. Pull vs. Hybrid

| Dimension | Push Model (Write-Time) | Pull Model (Read-Time) | Hybrid Model |
| :--- | :--- | :--- | :--- |
| **Write Complexity** | High ($O(N)$ write operations per post). | Low ($O(1)$ write operation per post). | Low/Medium ($O(1)$ for celebs, $O(N)$ for standard). |
| **Read Complexity** | Low ($O(1)$ retrieval). | High ($O(F \log(K))$ merge operation). | Low/Medium ($O(1)$ + lightweight merge). |
| **Best Suited For** | High read-to-write ratios, low variance in follower counts. | High write-to-read ratios, huge follower counts (celebrities). | Skewed topologies (Twitter/Instagram scale). |
| **Resource Usage** | High memory overhead (pre-built timelines for all users). | High CPU and DB Read IOPS at request time. | Balanced (efficient memory and CPU utilization). |
| **Data Consistency** | Eventual consistency during queue backlogs. | Real-time consistency (pulled directly from source). | Eventual consistency for standard feeds; instant for celebrity posts. |

---

## 💥 3. Resiliency & Operations

### Observability (The "Signal")
*   **Fan-out Queue Backlog / Lag:** Monitors the length of queues (e.g., Kafka topics, RabbitMQ queues) processing fan-out tasks. High lag indicates that workers are falling behind, resulting in delayed timeline updates.
*   **Timeline Write Latency (p95/p99):** The time elapsed between a user posting and their post appearing in followers' timelines. Spikes indicate write-path bottlenecks.
*   **Redis Hit Ratio & Eviction Rate:** Since feeds are cached, high eviction rates indicate insufficient memory allocation, forcing fallback reads to the persistent database.

### Blast Radius (The "Impact")
*   **Queue Starvation:** A sudden burst of celebrity posts in a pure push system will overwhelm worker nodes, backing up the processing queue and delaying updates for all users.
*   **Database Overload:** If the timeline cache fails (e.g., Redis cluster OOM), the system falls back to pulling and merging feeds from the primary database, resulting in a database connection freeze and cascading outages.
*   **Mitigation:** 
    1.  **Priority Queuing:** Route standard user writes and celebrity writes to isolated queue topics.
    2.  **Timeline Capping:** Limit the size of pre-computed feeds in Redis (e.g., cap at 500-800 items). Do not store infinite histories in memory.
    3.  **Graceful Degradation:** If cache is unavailable, fall back to serving a static/cached snapshot of the feed or limit retrieval to a smaller set of users.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   **Ignoring the Celebrity skew:** Proposing a simple push model for Twitter-scale systems without acknowledging that writing to 100M+ followers in a single transaction will lock resources and crash workers.
*   **Naive Runtime JOINs:** Suggesting a SQL `JOIN` across a `Followers` table, a `Posts` table, and a `Users` table at read-time to assemble the feed for active users, showing a lack of understanding of large-scale relational database latency.
*   **Infinite Cache Retention:** Forgetting to cap the size of cached timelines in Redis, leading to linear memory growth and high infrastructure costs.

### Interview Tip (The "Strong Hire" Signal)
> "In a high-scale feed system, a naive push or pull architecture is insufficient. I would implement a hybrid fan-out model with a dynamically adjusted threshold (e.g., 25,000 followers) to identify celebrities. Regular posts are fanned out on write to followers' Redis ZSets, capped at 800 items to limit memory consumption. Celebrity posts are pulled on read. To compile the user's feed, we retrieve their ZSet and perform an in-memory merge-sort with the celebrity feeds. Additionally, we track user activity, skipping fan-out for inactive users (inactive for > 30 days) to save memory and write cycles, lazily reconstructing their feed when they log back in."

---

## 💡 5. My Custom Study Notes & Whiteboard

### Memory Capping & LRU Policies

```
     Feed Cache (Redis ZSet)
 ┌─────────────────────────────┐
 │ Score (Epoch) │ Value (PID) │
 ├───────────────┼─────────────┤
 │ 1716498700    │ post:98342  │
 │ 1716498680    │ post:98321  │
 │ 1716498650    │ post:98109  │
 │ ...           │ ...         │ ◄── Keep only top K items (e.g., 800)
 └───────────────┴─────────────┘
  ZADD timeline:user123 1716498700 post:98342
  ZREMRANGEBYRANK timeline:user123 0 -801 (Evict oldest items beyond index 800)
```

*   **Active vs. Inactive Users:**
    *   Do not pre-compute timelines for inactive users.
    *   Maintain an "Active Users" registry (updated on login).
    *   If a follower is inactive, skip pushing to their Redis ZSet.
    *   When the user logs in after a period of inactivity, trigger a background worker to run a pull-based feed generation and populate their Redis cache.

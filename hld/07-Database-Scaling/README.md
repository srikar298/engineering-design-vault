# 🚀 07 - Database Scaling (The SDE-3 Edge)

## 📖 The Concept
When a single database instance cannot handle the read/write load or storage capacity, it must be scaled. This involves Replication (copying data) and Partitioning/Sharding (splitting data).

## 📊 The SDE-2 Trade-off Table: Scaling Strategies

| Strategy | Primary Goal | Pros | Cons (The Trade-off) |
| :--- | :--- | :--- | :--- |
| **Read Replicas** | Scale Reads. | Easy to setup. Offloads heavy SELECT queries. | **Replication Lag**. Reads might be stale. |
| **Partitioning (Sharding)** | Scale Writes & Storage. | Infinite scale by adding nodes. | Extremely complex to balance and query across shards (No easy JOINs). |
| **Multi-Leader** | Geographic Scale. | Low write latency for users globally. | **Write Conflicts** if two users update the same data in different regions. |

## 🚫 The Interview Trap
**"I will shard the database to handle the load."**
Sharding is a last resort, not a first step. It breaks foreign keys, makes JOINs impossible, and requires complex application-level routing.
*Better Answer:* "First, I would try to scale vertically. If that's insufficient, I'll add Read Replicas since our workload is read-heavy. If we hit the absolute ceiling on write throughput, *only then* will I explore sharding."

## 🚀 The SDE-3 Edge: The Celebrity Problem (Hot Keys)
If the interviewer asks: *"You sharded your social network by UserID. Cristiano Ronaldo posts a photo. What happens?"*

**The Trap:** All read traffic targets the single shard where Ronaldo's data lives, melting that specific server.

**The SDE-3 Solution:**
1. **Asymmetric Caching / Fan-out on Read:** For "Celebrity" users, pre-compute their timelines and push them to a massive Redis cluster. Normal users have their feeds computed on the fly (Fan-out on Write).
2. **Consistent Hashing with Virtual Nodes:** Ensure shards are evenly distributed, but accept that cache is the only way to survive a true "Hot Key" event.

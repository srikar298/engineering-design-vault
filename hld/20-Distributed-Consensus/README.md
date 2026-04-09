# 🤝 20 - Distributed Consensus (Raft & Paxos)

## 📖 1. The Concept
In a distributed system with 10k nodes, how do they all agree on a single value (e.g., "Who is the Master?") despite network failures? This is **Consensus**.

---

## 🏛️ 2. The Raft Algorithm: State Machine

An SDE-2 must explain how Raft handles failures through its three primary states.

```mermaid
stateDiagram-v2
    [*] --> Follower
    Follower --> Candidate : Heartbeat Timeout
    Candidate --> Candidate : Election Fail (Split Vote)
    Candidate --> Leader : Majority of Votes
    Leader --> Follower : Discover Higher Term
    Candidate --> Follower : Discover Leader/Higher Term
```

### The "Split Brain" Problem
What if two candidates start an election at the exact same time? 
- **The Solution:** **Randomized Election Timeouts**. This ensures that one candidate will likely time out before the other, starting its election and securing a majority while the other is still waiting.

---

## ⚡ 3. The SDE-3 Edge: Linearizability vs. Eventual Consistency
When would you *not* use Raft?
- **Trade-off:** Consensus adds **Latency** because every write must wait for a network round-trip to a majority ($N/2 + 1$) of nodes.
- **Decision:** If your scale (100k+ writes/sec) exceeds what a few nodes can handle, you might drop Consensus for **Eventual Consistency** (e.g., Gossip Protocol in DynamoDB).

**Senior Signal:** "We use **etcd** for our service discovery and leader election because its Raft implementation provides **Linearizable Reads**, ensuring that all components have a globally consistent view of the cluster state, preventing the catastrophic 'Dual Master' scenario."

---

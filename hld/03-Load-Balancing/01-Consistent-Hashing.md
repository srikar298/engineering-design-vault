# 🎡 C066 - Consistent Hashing

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C066 |
| **Category** | Load Balancing |
| **Difficulty** | 🟡 Medium |
| **Interview Frequency** | 🔥 High |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## 🎡 1. The Core Concept

### The Problem: Modulo Hashing Limits
In traditional distributed hashing, key-to-node mapping is computed using a simple modulo operation over the number of active nodes $N$:
$$\text{Node Index} = \text{hash}(key) \pmod N$$

While straightforward to implement, this approach breaks down during scaling events (adding or removing nodes). When the number of nodes changes from $N$ to $N+1$ (scale-out) or $N-1$ (scale-in), the denominator of the modulo operation changes. This shifts the target node for almost all keys.

#### Mathematical Impact of Modulo Rescaling:
Suppose we have a set of keys uniformly distributed across $N$ servers. When we add a new server, the new cluster size becomes $N+1$. A key originally hashing to node $H = \text{hash}(k) \pmod N$ will map to the same node in the new system if and only if:
$$\text{hash}(k) \pmod N \equiv \text{hash}(k) \pmod{N+1}$$

Mathematically, the fraction of keys that remain on their original nodes is only:
$$P(\text{no change}) \approx \frac{1}{N+1}$$
Conversely, the fraction of keys that must be reassigned is:
$$P(\text{reassignment}) = 1 - \frac{1}{N+1} = \frac{N}{N+1}$$

For example, if you scale a cluster from $N=4$ nodes to $N=5$ nodes:
$$P(\text{reassignment}) = \frac{4}{5} = 80\%$$
If you have 1,000,000 cached objects, **800,000 of them suddenly map to different servers**. In a distributed caching tier (e.g., Memcached), this causes a massive cache miss storm, instantly routing traffic to the backing database and causing potential database outages (the "thundering herd" problem). In a distributed storage tier, this requires moving 80% of all data across the network.

---

### The Solution: Consistent Hashing Ring
Consistent Hashing resolves this by mapping both the keys and the nodes to a shared, circular hash space (referred to as the **Hash Ring**), typically defined from $0$ to $2^{32} - 1$ (using 32-bit unsigned integers).

```
                      -- HASH RING (0 to 2^32 - 1) --
                                   0
                               .---*---.
                            .-'         '-.
                         .-'               '-.
                       .'                     '.
                      /   [S1]                  \
                     ;     *                     ;   [K1]
                    |     / \                     |   * (Routes to S2)
                    |    /   \                    |  /
                    |   /     \                   | v
                    |  /       \                  |
                    ;  *       *                  ;
                     \ [S4]   [S2]               /
                      '.               *       .'
                        '-.          [K2]   .-'
                           '-.           .-'
                              '---*---'
                                [S3]
```

1. **Hash the Nodes**: The identifiers of physical servers (e.g., IP addresses, hostnames) are hashed and placed at specific points on the ring.
2. **Hash the Keys**: The key of the data object is hashed using the same hash function to obtain a point on the ring.
3. **Route the Keys**: To locate the server for a given key, we traverse the ring clockwise starting from the key's hash position until we encounter the first server.
4. **Scale-Out / Add Node**: When a new server is added to the ring, it only claims keys situated between its position and its counter-clockwise neighbor. The rest of the ring remains unaffected.
5. **Scale-In / Node Failure**: If a server fails or is removed, its keys are reassigned to its immediate clockwise neighbor on the ring. All other node mappings remain unchanged.

---

### Key Redistribution Visualized

#### Case 1: Node Addition
```text
Original Ring: 
  ---(S1)------[K1]------(S2)------[K2]------(S3)---
  Mappings: K1 -> S2, K2 -> S3

Adding Node S_new between K1 and S2:
  ---(S1)------[K1]----(S_new)---(S2)------[K2]------(S3)---
  Mappings: K1 -> S_new (Reassigned!), K2 -> S3 (Unchanged)
```

#### Case 2: Node Failure
```text
Original Ring:
  ---(S1)------[K1]------(S2)------[K2]------(S3)---
  Mappings: K1 -> S2, K2 -> S3

Node S2 Fails:
  ---(S1)------[K1]------------------[K2]------(S3)---
  Mappings: K1 -> S3 (Reassigned to next clockwise node), K2 -> S3 (Unchanged)
```

---

### The Virtual Nodes (VNodes) Extension
Directly mapping physical nodes to the ring creates two major problems:
1. **Hotspots (Non-Uniform Distribution)**: Even if the hash function is uniform, mapping a small number of physical servers (e.g., $N=3$) will partition the ring into highly unequal segments. One server might cover 70% of the ring while another covers only 10%.
2. **Cascading Failure (The Avalanche Effect)**: If a server fails, its entire load shifts onto its immediate clockwise neighbor. This sudden load surge can crash the neighbor, causing it to fail and dump its load on the next server, creating a chain reaction.

**Virtual Nodes (vnodes)** solve this by representing each physical server as multiple points on the ring.

```text
Physical Nodes: [Node A, Node B]
VNode Factor: 3
Ring Placements:
  ---(A#1)---(B#1)---(A#2)---(B#2)---(A#3)---(B#3)---
```
When Node A fails, its virtual nodes (`A#1`, `A#2`, `A#3`) disappear. The keys mapped to them are redistributed to the next clockwise vnodes, which are interleaved between Node B's vnodes. As a result, the failed node's workload is distributed across all remaining physical nodes rather than a single neighbor.

---

## 🔍 2. Deep Dive

### Hash Function Selection
Consistent hashing requires a uniform, fast, and collision-resistant hash function.
* **Cryptographic Hashes (MD5, SHA-1)**: Used historically (e.g., in early Dynamo/Cassandra). MD5 produces a 128-bit output, while SHA-1 produces 160-bit. They are computationally expensive.
* **Non-Cryptographic Hashes (MurmurHash3, CityHash, FarmHash)**: Modern standard. They are significantly faster than MD5/SHA-1 and provide excellent distribution qualities, making them ideal for high-throughput ring operations.

---

### Mathematical Proof of Data Movement
Let:
* $K$ be the total number of keys.
* $N$ be the number of active nodes.
* $V$ be the number of virtual nodes per physical node (assumed constant for simplicity).

The total number of points on the ring is $M = N \times V$.
The hash ring partition size is governed by exponential distribution. The average fraction of the ring managed by any single node is $\frac{1}{N}$.

#### Case: Adding a Node
When we add a new node, the node count increases to $N+1$. The new node places $V$ virtual nodes randomly onto the ring.
* The expected fraction of the ring claimed by the new node is $\frac{1}{N+1}$.
* Because the keys are uniformly distributed across the hash space, the expected number of keys relocated to the new node is:
  $$\text{Keys Relocated} = K \times \frac{1}{N+1}$$
* The fraction of keys moved is:
  $$\text{Fraction Moved} = \frac{1}{N+1} \approx \frac{1}{N}$$

For a cluster scaling from 100 to 101 nodes, only $\approx 1\%$ of keys are relocated. Contrast this with the modulo hashing approach, which would relocate $\frac{100}{101} \approx 99\%$ of the keys.

---

### Virtual Node Density Analysis
The variance in key distribution across physical nodes decreases as the number of virtual nodes ($V$) per physical node increases.

The standard deviation of the load distribution ($\sigma$) is approximately:
$$\sigma \approx \frac{1}{\sqrt{V}}$$

| VNode Density ($V$) | Standard Deviation ($\sigma$) | Memory Overhead (per node) | Lookup Cost ($O(\log(N \cdot V))$) |
| :--- | :--- | :--- | :--- |
| **1** | $\approx 100\%$ (Highly uneven) | Negligible (1 entry) | Fast ($O(\log N)$) |
| **32** | $\approx 17.6\%$ | Low (32 entries) | Moderate |
| **128** | $\approx 8.8\%$ | Medium (128 entries) | Moderate |
| **256** | $\approx 6.25\%$ (Sweet Spot) | Balanced (~2KB) | Fast ($\approx 18$ ops for 1000 nodes) |
| **1024** | $\approx 3.1\%$ | High (~8KB) | Slightly slower |

**Trade-off**: Higher $V$ guarantees fairer load distribution but increases the memory size of the ring and search time ($O(\log(N \cdot V))$). In production systems, $V=256$ is typically chosen as the sweet spot.

---

### Concrete Implementation

#### Java Implementation (using `TreeMap` and `ReentrantReadWriteLock`)

```java
package com.designvault.loadbalancing;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConsistentHashRing<T> {
    private final int numberOfReplicas; // Number of vnodes per physical node
    private final TreeMap<Long, T> ring = new TreeMap<>();
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public ConsistentHashRing(int numberOfReplicas, Collection<T> nodes) {
        this.numberOfReplicas = numberOfReplicas;
        for (T node : nodes) {
            addNodeInternal(node);
        }
    }

    /**
     * Hashes a string key using MurmurHash3 (or fallback MD5 converted to 32-bit/64-bit).
     * For production, replace with a fast Murmur3 implementation.
     */
    private long hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(key.getBytes(StandardCharsets.UTF_8));
            // Convert first 8 bytes of hash to a long value
            long result = 0;
            for (int i = 0; i < 8; i++) {
                result = (result << 8) | (bytes[i] & 0xff);
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not found", e);
        }
    }

    public void addNode(T node) {
        rwLock.writeLock().lock();
        try {
            addNodeInternal(node);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private void addNodeInternal(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            String vNodeKey = node.toString() + "-vnode-" + i;
            ring.put(hash(vNodeKey), node);
        }
    }

    public void removeNode(T node) {
        rwLock.writeLock().lock();
        try {
            for (int i = 0; i < numberOfReplicas; i++) {
                String vNodeKey = node.toString() + "-vnode-" + i;
                ring.remove(hash(vNodeKey));
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public T route(String key) {
        rwLock.readLock().lock();
        try {
            if (ring.isEmpty()) {
                return null;
            }
            long hash = hash(key);
            if (!ring.containsKey(hash)) {
                // Get the tailMap starting from the key's hash
                SortedMap<Long, T> tailMap = ring.tailMap(hash);
                // If tailMap is empty, wrap around to the first key in the ring
                hash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
            }
            return ring.get(hash);
        } finally {
            rwLock.readLock().unlock();
        }
    }
}
```

---

#### Go Implementation (using Array Binary Search via `sort.Search`)

```go
package main

import (
	"crypto/md5"
	"encoding/binary"
	"fmt"
	"sort"
	"strconv"
	"sync"
)

type HashRing struct {
	vnodes      int
	ringHashes  []uint32          // Sorted list of vnode hashes
	nodeMapping map[uint32]string // Maps vnode hash to physical node name
	sync.RWMutex
}

func NewHashRing(vnodes int, nodes []string) *HashRing {
	hr := &HashRing{
		vnodes:      vnodes,
		nodeMapping: make(map[uint32]string),
	}
	for _, node := range nodes {
		hr.AddNode(node)
	}
	return hr
}

func (h *HashRing) hash(key string) uint32 {
	hasher := md5.New()
	hasher.Write([]byte(key))
	sum := hasher.Sum(nil)
	return binary.BigEndian.Uint32(sum[0:4])
}

func (h *HashRing) AddNode(node string) {
	h.Lock()
	defer h.Unlock()

	for i := 0; i < h.vnodes; i++ {
		vnodeName := node + "#" + strconv.Itoa(i)
		hashVal := h.hash(vnodeName)
		h.nodeMapping[hashVal] = node
		h.ringHashes = append(h.ringHashes, hashVal)
	}
	sort.Slice(h.ringHashes, func(i, j int) bool {
		return h.ringHashes[i] < h.ringHashes[j]
	})
}

func (h *HashRing) RemoveNode(node string) {
	h.Lock()
	defer h.Unlock()

	for i := 0; i < h.vnodes; i++ {
		vnodeName := node + "#" + strconv.Itoa(i)
		hashVal := h.hash(vnodeName)
		delete(h.nodeMapping, hashVal)
	}

	// Rebuild ringHashes array
	var newRingHashes []uint32
	for hashVal := range h.nodeMapping {
		newRingHashes = append(newRingHashes, hashVal)
	}
	sort.Slice(newRingHashes, func(i, j int) bool {
		return newRingHashes[i] < newRingHashes[j]
	})
	h.ringHashes = newRingHashes
}

func (h *HashRing) GetNode(key string) string {
	h.RLock()
	defer h.RUnlock()

	if len(h.ringHashes) == 0 {
		return ""
	}

	hashVal := h.hash(key)
	// Binary search to find the first index where ringHashes[idx] >= hashVal
	idx := sort.Search(len(h.ringHashes), func(i int) bool {
		return h.ringHashes[i] >= hashVal
	})

	// Wrap around to 0 if we reached the end of the ring
	if idx == len(h.ringHashes) {
		idx = 0
	}

	return h.nodeMapping[h.ringHashes[idx]]
}

func main() {
	ring := NewHashRing(10, []string{"Node-A", "Node-B", "Node-C"})
	fmt.Println("Key 'session_1234' ->", ring.GetNode("session_1234"))
	fmt.Println("Key 'user_99812'  ->", ring.GetNode("user_99812"))
}
```

---

## ⚖️ 3. Comparison Table & Trade-offs

| Strategy | Lookup Complexity | Ring Space State Size | Data Relocation on Add/Remove | Node Weights Support | Coordination Layer |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Modulo Hashing** | $O(1)$ | $O(1)$ (Only integer $N$) | Severe ($\approx \frac{N}{N+1}$) | Hard to implement | None (Stateless) |
| **Consistent Hashing (No VNodes)** | $O(\log N)$ | $O(N)$ | Minimal ($\approx \frac{1}{N}$) | Poor (Unbalanced sizes) | Minimal (Shared ring configuration) |
| **Consistent Hashing (With VNodes)** | $O(\log(N \cdot V))$ | $O(N \cdot V)$ | Minimal ($\approx \frac{1}{N}$) | Excellent (VNode count proportional to weight) | Minimal (Shared ring configuration) |
| **Rendezvous Hashing (HRW)** | $O(N)$ | $O(N)$ | Minimal ($\approx \frac{1}{N}$) | Easy (Weight multiplication factors) | None (Stateless, calculated per-request) |
| **Directory-Based Lookup** | $O(1)$ | $O(K)$ | Zero (Explicit movement) | Perfect (Explicit routing) | Heavy (Centralized Metadata Database, e.g., ZooKeeper) |

### Key Trade-offs:
1. **Memory vs Uniformity**: Virtual nodes require storing additional hash keys. A high number of vnodes improves distribution balance but increases ring memory footprint.
2. **Search Latency**: Instead of direct index access ($O(1)$), consistent hashing requires binary search ($O(\log M)$). For extremely high-throughput proxies (million+ RPS), this binary search lookup time can become a bottleneck.
3. **Replication Coordination**: To support HA (High Availability), the client must route writes to multiple distinct physical servers. On a virtual node ring, simply writing to the next $R$ vnodes clockwise could lead to replicating data on the same physical server (if adjacent vnodes belong to the same machine). The routing layer must filter out duplicate physical nodes to ensure physical isolation of replicas.

---

## 🏢 4. Real-world Usage

### Apache Cassandra
* **Mechanism**: Cassandra uses consistent hashing to distribute data partitions across the cluster ring. The hash of a row's partition key determines its primary node.
* **Token Ring**: Nodes are assigned tokens on a 128-bit integer ring space (using the `Murmur3Partitioner`).
* **VNodes**: Cassandra introduced virtual nodes (defaulting to 128 or 256 per physical machine) to simplify rebalancing. When a new node is added, it takes a small number of virtual tokens from every existing node, ensuring uniform data migration without manual resharding.

### Amazon DynamoDB
* **Mechanism**: Consistent hashing distributes data items across partitions. Dynamo's original architecture was built on a consistent hashing ring with virtual nodes to handle varying server hardware performance (assigning more vnodes to faster machines).

### Akamai Content Delivery Network (CDN)
* **Mechanism**: Akamai uses consistent hashing to assign web content to its edge cache servers. This minimizes cache eviction when edge nodes churn or fail, preventing request spikes from routing to origin servers.

### HAProxy & Envoy Proxy
* **Mechanism**: High-performance HTTP proxies offer `ring-hash` routing. This ensures that requests with the same identifier (e.g., HTTP header, User ID) always hit the same backend server (sticky sessions), while minimizing session loss during backend autoscaling events.

---

## 💬 5. SDE-2 Interview Script

### The Scenario:
*The interviewer asks you to design a distributed caching layer (like Memcached) to handle 10,000 requests per second. The system must scale horizontally as traffic grows.*

#### Step 1: Establish the problem with traditional hashing
* **Candidate**: "To distribute keys across a cluster of caching nodes, we could use standard modulo hashing, like `hash(key) % N`. However, this approach has a major scalability limitation. If we need to scale from $N=9$ to $N=10$ nodes to handle peak load, the denominator changes. As a result, approximately $\frac{9}{10}$ (or 90%) of our keys will map to different nodes. This triggers a sudden cache eviction storm across the entire tier, overloading our databases and causing latency spikes."

#### Step 2: Propose Consistent Hashing and explain the ring
* **Candidate**: "To resolve this, I will implement a Consistent Hashing Ring. We will map both the caching servers and the cache keys to a shared circular space, for example, from $0$ to $2^{32}-1$. We use a uniform hash function like MurmurHash3. A key is routed to the first server node encountered moving clockwise from the key's hash position. If we add a node, it only claims keys between itself and its counter-clockwise neighbor. The expected fraction of data moved is only $\frac{1}{N+1}$."

#### Step 3: Propose VNodes to solve distribution skew
* **Interviewer**: "That makes sense. But what if the servers are hashed to positions very close to each other on the ring? Won't that create a load imbalance?"
* **Candidate**: "Yes, that is a classic problem with basic consistent hashing. Since server placements are random, we can end up with highly unequal partitions, leading to hotspot servers. To solve this, I will implement Virtual Nodes (vnodes). Instead of placing each physical server once on the ring, we will place it $V$ times (e.g., $V=256$) using virtual identifiers like `IP:Port#1`, `IP:Port#2`, etc. This interleaves the physical servers' coverage across the ring. If a physical node fails, its $V$ virtual nodes disappear, and its workload is distributed evenly among all remaining physical nodes instead of overloading just one neighbor."

#### Step 4: Detail the routing logic and data structures
* **Interviewer**: "How does the lookup work in code? What data structures would you use?"
* **Candidate**: "In code, we can store the sorted list of vnode hashes in an array or a balanced binary search tree, like a `TreeMap` in Java. When a request arrives with a key, we hash the key, and perform a binary search (specifically, a ceiling or `bisect_right` operation) to find the first hash value in our sorted list that is greater than or equal to the key's hash. If we reach the end of the array, we wrap around to index 0. The time complexity of this lookup is $O(\log(N \cdot V))$."

#### Step 5: Address Replication & Concurrency
* **Interviewer**: "How do you handle replication on this ring?"
* **Candidate**: "If we want to store replicas of a key (say replication factor $R=3$), we don't stop at the first vnode. We continue moving clockwise along the ring to collect the next $R$ nodes. However, we must skip vnodes that belong to physical machines we've already selected. This ensures that replicas are stored on distinct physical servers to maintain fault tolerance."

---

## ✅ 6. SDE-2+ Readiness Checklist

- [ ] Explain why standard modulo hashing causes $\frac{N}{N+1}$ key reassignments during scaling.
- [ ] Draw the consistent hashing ring, node addition, and node failure scenarios.
- [ ] Detail the mathematical proof showing why consistent hashing reduces data movement to $\approx \frac{1}{N}$.
- [ ] Explain the hotspots problem and how Virtual Nodes (vnodes) mitigate it.
- [ ] Analyze the trade-offs of vnode density (e.g., $V=256$) on memory footprint vs. standard deviation of load distribution.
- [ ] Implement a thread-safe Consistent Hash Ring in Java (using `TreeMap`) or Go (using `sort.Search`).
- [ ] Address replication edge cases on the ring (skipping duplicate physical nodes for adjacent vnodes).
- [ ] Compare consistent hashing with Rendezvous Hashing (HRW) and Directory-based partitioning.
- [ ] Cite real-world applications of consistent hashing (e.g., Apache Cassandra, DynamoDB, Akamai, Envoy).

# 🧩 Data Structures at Scale (C073, C074)

In distributed systems, traditional data structures aren't enough. We need structures that prioritize **efficiency**, **approximate answers**, and **minimal data transfer**.

---

## 1. Bloom Filters (C073)

**Concept:** A space-efficient probabilistic data structure used to test whether an element is a member of a set.
- **False Positives:** Possible (says "Yes" but element isn't there).
- **False Negatives:** IMPOSSIBLE (If it says "No", the element is definitely NOT there).

**Use Cases:**
- **Cache Penetration:** Check if a key exists in the DB *before* querying it.
- **Cassandra/HBase:** Skip checking shards/files that definitely don't contain the data.
- **Web Crawlers:** Avoid re-crawling millions of URLs.

---

## 2. Merkle Trees (Hash Trees) (C074)

**Concept:** A tree where every leaf node is a hash of a data block, and every non-leaf node is a hash of its children.

**Why use it?**
- **Efficient Anti-Entropy (Data Sync):** Two nodes can compare their Merkle Trees. If the root hashes are the same, the data is identical. If different, they only need to traverse the branches that differ to find the specific out-of-sync blocks.

**Use Cases:**
- **Cassandra/DynamoDB:** Syncing replicas with minimal network transfer.
- **Git:** Identifying changed files.
- **Blockchain:** Verifying transactions in a block.

---

## 🧠 Tracker Integration (C073, C074)

*   **Core Trade-off:** Memory vs Accuracy (Bloom Filters).
*   **The "Senior Signal":** Explaining how Merkle Trees reduce network bandwidth during database replication recovery.
*   **Interview Trap:** Suggesting a Bloom Filter for something that requires 100% accuracy (like deleting a user).

### 🔬 Self-Assessment Prompts
1. How do Bloom Filters handle "Deletion"? (Hint: They don't easily—you need Counting Bloom Filters).
2. Why is the Root Hash of a Merkle Tree so powerful for security?
3. In a 1TB database, how much bandwidth does a Merkle Tree save during a sync compared to sending all keys?

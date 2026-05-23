# ⚡ 16 - Bloom Filters

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C030 |
| **Category** | Distributed Data Structures |
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
*   **Two-Sentence Trigger:** A Bloom Filter is a space-efficient probabilistic data structure used to test whether an element is a member of a set. It can return false positives (indicating an element might be in the set) but never false negatives (guaranteeing an element is definitely not in the set), preventing unnecessary disk reads or cache misses.
*   **Scalability Dimension:** Primary: **Read Latency (prevents disk access)** & **Memory Overhead Reduction**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### How the Bit Array Works
1. **Initialization:** A bit array of size $m$ is initialized to 0. We define $k$ independent hash functions.
2. **Insertion:** When an element is added, it is processed through the $k$ hash functions to generate $k$ bit offsets, and those bits are set to 1.
3. **Query:** To check if an element exists, it is processed through the $k$ hash functions. If **any** of the resulting bits is 0, the element is **definitely not** in the set. If all bits are 1, the element **might** be in the set (due to hash collisions).

```
Bit Array: [0, 1, 0, 1, 1, 0, 0, 1]
Query "KeyA" hashes to bits [1, 3] -> Both are 1 -> "Maybe Exists"
Query "KeyB" hashes to bits [0, 5] -> Bit 5 is 0  -> "Definitely Does Not Exist" (Reject instantly)
```

### Trade-offs
*   **Pros:** Space-efficient. Storing 100M keys takes megabytes instead of gigabytes. $O(k)$ time complexity is independent of the number of items stored.
*   **Cons:** You cannot delete items from a standard Bloom Filter (setting a bit to 0 deletes other keys sharing that hash slot). *Solution:* Use a **Counting Bloom Filter** (uses integer counters instead of single bits, which increases memory size).

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `False Positive Rate (FPR)`: If the bit array fills up (> 50% of bits are 1), the false positive rate spikes, letting invalid queries reach the database.
*   **Blast Radius (The "Impact"):**
    *   If a Bloom Filter is corrupted or runs out of capacity, it loses its filtering power, causing a cache penetration storm that degrades database read performance.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Attempting to delete items from a standard Bloom Filter without explaining the hash collision corruption side-effect.
*   Assuming Bloom Filters are 100% accurate (you must design your system to tolerate the configured false positive rate, e.g., 1%).

### Interview Tip (The "Strong Hire" Signal)
> *"To prevent cache penetration attacks on `/user/{id}`, we run incoming IDs through a Bloom Filter in-memory. If it yields a negative, we return a 404 immediately. This saves our Redis and DB layers from executing queries for non-existent keys."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

# Bloom Filters

| Field       | Value                                              |
|-------------|----------------------------------------------------|
| Concept ID  | C073                                               |
| Category    | Probabilistic Data Structures / Caching            |
| Difficulty  | 🟡 Medium                                          |
| Frequency   | 🔥 High (asked at Google, Meta, Amazon, Confluent) |

---

## 1. The Core Concept

### The Problem: Membership in a Huge Set

Imagine you're building:
- **Chrome Safe Browsing**: Is this URL in a database of 300 million known malicious URLs?
- **Cassandra / HBase**: Does this key exist in this SSTable before I do an expensive disk read?
- **Username registration**: Is this username already taken (before hitting the DB)?
- **Email spam filter**: Has this user already seen this email?
- **Web crawler**: Have I already visited this URL?

**Naive approaches and their costs:**

| Approach                      | Space               | Lookup Time | Supports Delete? | Notes                              |
|-------------------------------|---------------------|-------------|------------------|------------------------------------|
| Exact set (HashSet)           | O(N × item_size)    | O(1)        | ✅               | 300M URLs × ~50 bytes = 15 GB RAM  |
| Sorted array + binary search  | O(N × item_size)    | O(log N)    | ❌ (expensive)   | CPU-efficient but still huge       |
| Bloom Filter                  | O(m) bits ≈ m/8 KB  | O(k)        | ❌ (basic)       | ~10 bits/item for 1% FP rate       |

For 300 million URLs at a 0.1% false positive rate, a Bloom filter needs roughly **~4 GB** — far less than the 15 GB required to store the URLs themselves. And crucially, the Bloom filter **never** needs to store the actual URLs.

**The fundamental trade-off:** Accept a small, tunable probability of false positives in exchange for dramatic space savings and O(k) lookups.

```
THE MEMBERSHIP PROBLEM:

  You have set S = {x1, x2, ..., xn}  (too large to store exactly)
  Query: "Is element q in S?"

  Two possible answers:
  ┌─────────────────────────────────────────────────────────┐
  │  "NO"  → Guaranteed correct. q is DEFINITELY not in S. │
  │  "YES" → Probably correct, but could be a false pos.   │
  └─────────────────────────────────────────────────────────┘

  Use case fit:
  ✅ Great when "NO" answers let you skip expensive work (disk I/O, DB call)
  ✅ Great when false positives are tolerable (a URL gets double-checked)
  ❌ Bad when false positives are catastrophic (medical diagnoses)
```

---

## 2. Deep Dive

### 2.1 How a Bloom Filter Works

A Bloom filter is a **bit array** of `m` bits (initially all 0) plus `k` independent hash functions.

**INSERT an element x:**
1. Compute `h1(x), h2(x), ..., hk(x)` — each gives a position in [0, m-1]
2. Set bits at all k positions to 1

**QUERY for element x:**
1. Compute `h1(x), h2(x), ..., hk(x)`
2. Check all k positions
3. If **all** bits are 1 → return "PROBABLY IN SET"
4. If **any** bit is 0 → return "DEFINITELY NOT IN SET"

```
BIT ARRAY WITH k=3 HASH FUNCTIONS, m=18 bits:

Bit positions: 0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17
Initial state: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  0,  0,  0,  0,  0,  0,  0,  0]

INSERT "alice":
  h1("alice") = 1  → set bit 1
  h2("alice") = 5  → set bit 5
  h3("alice") = 13 → set bit 13

After insert "alice":
           [0, 1, 0, 0, 0, 1, 0, 0, 0, 0,  0,  0,  0,  1,  0,  0,  0,  0]
                ↑              ↑                             ↑

INSERT "bob":
  h1("bob") = 4  → set bit 4
  h2("bob") = 7  → set bit 7
  h3("bob") = 13 → set bit 13 (already set — collision, that's OK)

After insert "bob":
           [0, 1, 0, 0, 1, 1, 0, 1, 0, 0,  0,  0,  0,  1,  0,  0,  0,  0]
                      ↑     ↑

QUERY "alice":
  h1("alice")=1 → bit[1]=1 ✅
  h2("alice")=5 → bit[5]=1 ✅
  h3("alice")=13→ bit[13]=1 ✅
  → All set → "PROBABLY IN SET" ✅ (correct positive)

QUERY "charlie":
  h1("charlie")=2 → bit[2]=0 ❌
  → Any 0 → "DEFINITELY NOT IN SET" ✅ (correct negative)

QUERY "dave":
  h1("dave")=1  → bit[1]=1 ✅
  h2("dave")=7  → bit[7]=1 ✅
  h3("dave")=13 → bit[13]=1 ✅
  → All set → "PROBABLY IN SET" ⚠️ (FALSE POSITIVE — dave was never inserted!)
  This happens because dave's hash positions collide with alice's and bob's.
```

### 2.2 False Positives — Mathematics

**Why can't false negatives occur?**
If an element x was inserted, all k of its bit positions were set to 1. When you query x, those bits are still 1 (bits are never cleared in a basic Bloom filter). So the filter will always return "YES" for inserted elements.

**Why do false positives occur?**
As more elements are inserted, more bits get set to 1. Eventually, a new element's k hash positions all happen to already be set by *other* elements' insertions. The filter incorrectly reports membership.

**False Positive Probability Formula:**

After inserting `n` elements into a bit array of `m` bits with `k` hash functions:

```
Each hash function sets a random bit.
Probability a specific bit is NOT set by one hash of one insertion:
  P(bit stays 0) = (1 - 1/m)

After n insertions with k hash functions (k*n individual hash operations):
  P(bit is still 0) = (1 - 1/m)^(kn)

Probability all k bits for a query ARE set (false positive):
  P(false positive) = (1 - (1 - 1/m)^(kn))^k

Using limit approximation (1 - 1/m)^m ≈ e^(-1):
  P(FP) ≈ (1 - e^(-kn/m))^k
```

**Numeric examples (n=1M items):**

| m/n (bits per item) | k (optimal) | P(false positive) |
|---------------------|-------------|-------------------|
| 8                   | 6           | ~2.16%            |
| 10                  | 7           | ~0.82%            |
| 14                  | 10          | ~0.11%            |
| 20                  | 14          | ~0.006%           |
| 23                  | 16          | ~0.001%           |

**Rule of thumb:** ~10 bits per item gives ~1% false positive rate.

### 2.3 Optimal Sizing Formulas

Given desired false positive rate `p` and expected number of items `n`:

```
Optimal bit array size m:
  m = -n * ln(p) / (ln(2))^2
  m ≈ -1.44 * n * log2(p)

Optimal number of hash functions k:
  k = (m/n) * ln(2)
  k ≈ 0.693 * (m/n)

Derivation intuition:
  Too few k → each element sets too few bits → false positives drop, but
              each bit less likely set → actually improves FP rate
  Too many k → fills the array faster → more false positives
  Sweet spot: k = (m/n) * ln(2) minimizes FP probability
```

**Practical Calculator:**
```
  Target: n = 1,000,000 items, p = 0.01 (1% false positive rate)
  
  m = -1,000,000 × ln(0.01) / (ln 2)^2
    = -1,000,000 × (-4.605) / 0.480
    = 9,585,058 bits
    ≈ 9.6 Mbits ≈ 1.2 MB  ← Store 1M items in just 1.2 MB!

  k = (9,585,058 / 1,000,000) × ln(2)
    = 9.585 × 0.693
    ≈ 7 hash functions

Compare to HashSet: 1M strings × ~50 bytes avg = ~50 MB
Bloom filter: 1.2 MB — 40× smaller!
```

### 2.4 Hash Functions in Practice

The `k` hash functions must be:
- Fast to compute
- Independent (or at least pairwise independent)

**Double Hashing Trick:** Instead of k truly independent hash functions, compute just two (`h1` and `h2`) and generate k positions as:
```
  g_i(x) = (h1(x) + i * h2(x)) mod m   for i = 0, 1, ..., k-1
```
This is space-efficient and performs well in practice. MurmurHash and FNV are common choices.

**Practical libraries use:** xxHash, MurmurHash3 — both are non-cryptographic, extremely fast.

---

### 2.5 Counting Bloom Filters (CBF)

**Problem with basic Bloom filters:** No deletion. Setting bits to 0 is unsafe — another element might share that bit position. Clearing it would create false negatives.

**Counting Bloom Filter:** Replace each bit with a small integer counter (typically 4 bits = values 0–15).

```
OPERATION         BASIC BLOOM        COUNTING BLOOM
─────────────────────────────────────────────────────
Insert x         Set k bits → 1     Increment k counters
Delete x         ❌ Not supported    Decrement k counters
Query x          Check k bits=1     Check k counters > 0
Space            m bits             4m bits (4× larger)
False positives  Yes                Yes (same rate)
False negatives  Never              Never*

* Unless counter overflows (wraps around). 4-bit counter handles up to 16
  simultaneous items hashing to same position — adequate for most workloads.
```

**Counter Overflow:** If 4-bit counters overflow (>15 insertions map to same slot), a delete might make the counter drop to 0 prematurely, causing a false negative. Mitigation: use 8-bit (byte) counters or detect and handle overflow by not decrementing saturated counters.

```
COUNTING BLOOM — DELETE EXAMPLE:

After inserting "alice" (positions 1, 5, 13) and "bob" (positions 4, 7, 13):
  Counters: [0, 1, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0]
                                                         ↑
                                               position 13 has count=2
                                               (both alice and bob hash here)

DELETE "alice" (positions 1, 5, 13):
  Decrement counters at 1, 5, 13:
  Counters: [0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0]
                ↑              ↑                         ↑
  Position 13 drops to 1 — "bob" still correctly registered.

QUERY "alice" after delete:
  Position 1 → counter=0 ❌
  → "DEFINITELY NOT IN SET" ✅ (correct, alice was deleted)
```

---

### 2.6 Cuckoo Filters — A Better Alternative

Cuckoo filters (Fan et al., 2014) address Bloom filter's two main weaknesses: no deletion support and poor cache performance.

**How Cuckoo Filters Work:**

A Cuckoo filter stores small **fingerprints** (e.g., 8-bit hashes of items) in a compact hash table using cuckoo hashing. Each item has two candidate bucket positions.

```
INSERT x:
  f = fingerprint(x)           ← 8-bit truncated hash
  i1 = hash(x) mod m
  i2 = i1 XOR hash(f)         ← "alternate" bucket via XOR trick

  If i1 or i2 has empty slot → store f there → done
  Else: pick one, evict existing fingerprint, relocate it (cuckoo-style)
  If too many evictions (cycle) → filter is full → rebuild

QUERY x:
  f = fingerprint(x)
  i1 = hash(x) mod m
  i2 = i1 XOR hash(f)
  Return YES if f found in bucket i1 or i2, else NO

DELETE x:
  f = fingerprint(x)
  i1 = hash(x) mod m
  i2 = i1 XOR hash(f)
  Remove one copy of f from i1 or i2 → done!
```

**Key: XOR Trick for Symmetric Lookup**
Given bucket index `i`, you can find the alternate as `i XOR hash(fingerprint)`.
This means you can relocate items without knowing the original key — only the fingerprint is needed. This makes deletion safe.

**Cuckoo vs Bloom Comparison:**

| Property                  | Basic Bloom Filter | Counting Bloom Filter | Cuckoo Filter       |
|---------------------------|--------------------|-----------------------|---------------------|
| Space (1% FP rate)        | ~9.6 bits/item     | ~19 bits/item (4×)    | ~12 bits/item       |
| Lookup time               | O(k)               | O(k)                  | O(1) — 2 lookups    |
| Delete support            | ❌                  | ✅                     | ✅                   |
| Cache performance         | Poor (k random)    | Poor (k random)       | Good (2 buckets)    |
| Max occupancy             | ~100%              | ~100%                 | ~95%                |
| False positives           | Yes                | Yes                   | Yes                 |
| False negatives           | Never              | Never*                | Never (if no bugs)  |
| Implementation complexity | Simple             | Moderate              | Moderate            |

**Winner:** Cuckoo filters outperform Bloom filters in most metrics except simplicity. They're increasingly preferred in new systems.

---

### 2.7 Scalable Bloom Filters

**Problem:** Classic Bloom filters have fixed capacity. If you exceed `n` items, the false positive rate degrades.

**Scalable Bloom Filter (SBF):** Chain multiple Bloom filters, each larger than the previous (by a growth factor `s`). When the current filter's estimated occupancy exceeds a threshold, add a new filter.

```
SBF Structure:
  Filter 0: m0 bits, k0 hashes, FP rate p0
  Filter 1: m1=m0×s bits, k1 hashes, FP rate p0×r
  Filter 2: m2=m1×s bits, k2 hashes, FP rate p1×r
  ...

Query: check ALL filters (element might be in any)
  Overall FP rate: p_total = p0 * r/(1-r)  ← bounded if r < 1
```

This allows unbounded growth at the cost of multiple filter lookups.

---

### 2.8 Real-World Production Usage

**Apache Cassandra:**
```
CASSANDRA SSTable Bloom Filter Flow:

Read path:
  Client: GET key "user:12345"
           │
           ▼
  MemTable check (in-memory write buffer)
           │
           ├──── FOUND? → Return immediately ✅
           │
           ▼ Not in MemTable
  Bloom Filter per SSTable (multiple SSTables on disk)
           │
    For each SSTable:
    ┌─────────────────────────────────────────────┐
    │  Bloom filter says NO?                       │
    │  → Skip this SSTable entirely (no disk I/O) │
    │                                             │
    │  Bloom filter says YES?                     │
    │  → Check index, fetch block from disk       │
    └─────────────────────────────────────────────┘
           │
  Merge results across SSTables

WHY IT MATTERS:
  A typical Cassandra node has 10–100+ SSTables per table.
  Without Bloom filters: every read = 10–100 disk seeks.
  With Bloom filters (1% FP rate): ~1.01 disk seeks on average.
  → Orders-of-magnitude reduction in disk I/O.

Cassandra's bloom_filter_fp_chance: configurable per table (default 0.1%).
Lower FP chance → larger filter → more RAM → fewer disk reads.
```

**Google Chrome Safe Browsing:**
Chrome downloads a Bloom filter of ~300 million known malicious URLs (hosted by Google). Every URL you visit is checked locally against this filter instantly. Only if the filter says "YES" does Chrome make a network request to Google for verification. This keeps browsing private (most URLs never sent to Google) and fast (no network round-trip for safe URLs).

**Redis RedisBloom Module:**
```
Commands:
  BF.RESERVE myfilter 0.01 100000   # 1% FP rate, 100K items
  BF.ADD myfilter "user:abc"
  BF.EXISTS myfilter "user:abc"     # → 1 (probably yes)
  BF.EXISTS myfilter "user:xyz"     # → 0 (definitely no)
  BF.MADD myfilter item1 item2 ...  # batch add
  BF.MEXISTS myfilter i1 i2 ...     # batch query
```
RedisBloom also supports Cuckoo filters (CF.*), Count-Min Sketches, Top-K, and HyperLogLog.

**HBase / BigTable:**
Both use per-region Bloom filters for similar reasons as Cassandra — to avoid disk seeks on Get operations. HBase supports ROW-level and ROWCOL-level Bloom filters, allowing key-only or key+column-family filtering.

**Ethereum / Bitcoin:**
Bitcoin's SPV (Simplified Payment Verification) wallets use Bloom filters. A thin client sends its address's Bloom filter to a full node. The full node filters transactions through it and only sends matching ones — reducing bandwidth while preserving privacy (the full node doesn't know exactly which addresses the client owns).

**Akamai CDN:**
Uses Bloom filters to detect "one-hit wonders" — URLs requested only once. If a URL is in the filter (second request), it gets cached. If not, it's a first request → added to filter but not cached. This saves cache capacity from being dominated by one-time-access content.

---

## 3. Comparison Table

| Data Structure         | Space         | False Pos | False Neg | Delete | Exact? | Best Use Case                      |
|------------------------|---------------|-----------|-----------|--------|--------|------------------------------------|
| HashSet (exact)        | O(n × size)   | ❌         | ❌         | ✅     | ✅     | Small sets, need exact membership  |
| Sorted Array           | O(n × size)   | ❌         | ❌         | ❌     | ✅     | Static sets, memory-mapped         |
| Bloom Filter           | ~10 bits/item | Yes (~1%) | Never     | ❌     | ❌     | Huge sets, can tolerate FP         |
| Counting Bloom Filter  | ~40 bits/item | Yes (~1%) | Rare*     | ✅     | ❌     | Need delete, OK with 4× overhead   |
| Cuckoo Filter          | ~12 bits/item | Yes (~1%) | Never     | ✅     | ❌     | Need delete, want best perf        |
| XOR Filter (new)       | ~9 bits/item  | Yes (~1%) | Never     | ❌     | ❌     | Static sets, best space efficiency |
| Quotient Filter        | ~10 bits/item | Yes (~1%) | Never     | ✅     | ❌     | Need delete + cache friendliness   |

---

## 4. Real-World Usage (Summary)

| Company/Project | Filter Type       | Purpose                                    |
|-----------------|-------------------|--------------------------------------------|
| Cassandra       | Standard Bloom    | Skip SSTables on read (avoid disk seeks)   |
| HBase           | Standard Bloom    | Skip HFiles on Get operations              |
| Chrome          | Standard Bloom    | Safe Browsing — local malicious URL check  |
| Bitcoin SPV     | Standard Bloom    | Thin client transaction filtering          |
| Redis           | Bloom + Cuckoo    | RedisBloom module, general membership      |
| Akamai          | Standard Bloom    | One-hit wonder detection for cache         |
| PostgreSQL       | Standard Bloom    | Index type for equality queries            |
| Medium          | Standard Bloom    | Already-seen article recommendation filter |
| Ethereum        | Standard Bloom    | Transaction logs filtering (per block)     |

---

## 5. SDE-2 Interview Script

> **Q: "How would you check if a URL is malicious without storing all URLs in memory? Say you have 300 million known bad URLs."**

**Opening — Quantify the problem:**
> "Let me first understand the scale. 300 million URLs, average maybe 50 bytes each — that's about 15 GB just to store them. A HashSet lookup would work but requires 15 GB of RAM. And we might be checking billions of URLs per day. I want something that trades a little accuracy for a huge reduction in memory."

**Introduce Bloom Filters:**
> "A Bloom filter is a probabilistic data structure. It uses a bit array of `m` bits and `k` hash functions. To add a URL, I run it through all k hash functions, get k positions in the bit array, and set all those bits to 1. To query, I check if all k positions are set. If any is 0, the URL is definitely not malicious. If all are 1, it's probably malicious."

**Explain the trade-off:**
> "The beauty is what it can guarantee: it never gives false negatives. If the filter says 'no', the URL is safe — guaranteed. It might give false positives — saying a safe URL looks malicious. But I can handle that: if the filter says 'yes', I do a secondary lookup against a server or exact database to confirm. That's exactly how Chrome's Safe Browsing works."

**Give the math:**
> "Sizing is straightforward: for 300M URLs at a 1% false positive rate, I need roughly m = -n × ln(p) / (ln 2)² ≈ 2.9 billion bits ≈ 360 MB. And about 7 hash functions. Compare that to 15 GB for exact storage — that's a 40× reduction."

**Limitations and alternatives:**
> "Standard Bloom filters don't support deletion. If a URL is cleared from the malicious list, I can't remove it — I'd need to rebuild the filter. If deletion is important, I'd use a Counting Bloom Filter (uses counters instead of bits, allowing decrements) or a Cuckoo Filter, which has similar space requirements, supports deletion, and has better cache performance because it does only 2 memory lookups instead of k."

**Production context:**
> "This is exactly the approach Cassandra uses for SSTables. Each SSTable has a Bloom filter. Before reading from disk, Cassandra checks the filter. If it says 'no', we skip the entire SSTable. This reduces disk I/O by orders of magnitude in read-heavy workloads."

---

## 6. SDE-2+ Readiness Checklist

- [ ] Can explain the membership problem and why exact sets are expensive at scale
- [ ] Can describe the bit-array-plus-k-hash-functions structure from memory
- [ ] Can explain why false positives are possible but false negatives are impossible
- [ ] Can state the false positive probability formula: `P ≈ (1 - e^(-kn/m))^k`
- [ ] Can derive optimal m and k given n and desired p
- [ ] Can give a numeric example: 1M items, 1% FP rate → ~1.2 MB, 7 hash functions
- [ ] Can explain the double-hashing trick for generating k positions from 2 hash functions
- [ ] Can explain Counting Bloom Filters and when to use them (need deletes)
- [ ] Can explain Cuckoo Filters: fingerprints, XOR trick, two-bucket lookup, deletion support
- [ ] Can compare Bloom vs Counting Bloom vs Cuckoo on space, speed, and delete support
- [ ] Can explain exactly how Cassandra uses Bloom filters on the read path
- [ ] Can explain Chrome Safe Browsing as a Bloom filter application
- [ ] Can discuss Scalable Bloom Filters for unbounded growth
- [ ] Can explain Bitcoin SPV Bloom filter privacy model
- [ ] Can discuss counter overflow risk in Counting Bloom Filters
- [ ] Can name XOR Filters and Quotient Filters as newer alternatives
- [ ] Can explain why Bloom filters work well as a "gatekeeper" before expensive operations
- [ ] Can articulate the design decision: lower FP rate ↔ larger filter ↔ more RAM
- [ ] Can name at least 5 real-world systems that use Bloom filters in production

# B-Trees vs. LSM-Trees (C027-C028) | Category: Databases | Difficulty: 🔴 Hard | Frequency: 🔥 High

## 1. The Core Concept

### Database Storage Engines: The Physical I/O Bottleneck
At the heart of every database is the storage engine, which translates logical CRUD operations into physical disk operations. Because physical disk I/O (even on high-speed NVMe SSDs) is orders of magnitude slower than RAM operations, the physical disk access pattern dictates database throughput, latency, and hardware lifespan.

Disk storage media is structured:
* **Sectors/Blocks**: The smallest physical addressable unit on disk (typically 512 bytes to 4KB).
* **NAND Flash Pages and Blocks (SSDs)**: SSDs read and write in Pages (typically 4KB to 16KB), but can only erase in Blocks (typically 2MB to 8MB). Writing to an already occupied page requires reading the entire block, erasing it, and rewriting the modified block. This is called the **Read-Modify-Write cycle**, which leads to write degradation and physical cell wearing.

Therefore, storage engines are optimized around how they schedule physical writes:
1. **Random Disk I/O**: Accessing arbitrary, non-contiguous page offsets on disk. This is highly inefficient due to seek times (HDDs) and Flash Translation Layer (FTL) garbage collection overhead (SSDs).
2. **Sequential Disk I/O**: Appending data continuously to contiguous sectors on disk. This maximizes hardware bandwidth and avoids FTL read-modify-write bottlenecks.

---

### B-Trees: In-Place Updates
B-Trees (specifically $B^+$-Trees) are optimized for **read-heavy workloads**. They store data in fixed-size blocks (typically 8KB pages) and perform **in-place updates**. When a record is updated or inserted, the database locates the specific disk page containing the key, modifies the page in memory (buffer pool), and eventually overwrites the original disk page at its exact physical offset.

```
                      B-TREE NODE INDEXING & LAYOUT
                      
                               [ Root Page ]
                               [  20 | 50  ]
                              /     |      \
                             /      |       \
                            v       v        v
                 [ Internal Page ]  ...  [ Internal Page ]
                 [   5  |  15    ]       [  60  |  80    ]
                /       |        \
               v        v         v
           [Leaf 1]  [Leaf 2]  [Leaf 3]  <--- Sibling Leaf Pointers
           [1..4] ── [5..14] ── [15..19]
           
    Detailed Leaf Page Layout (8KB):
    ┌──────────────────────┬──────────────────────────────┬────────────────┐
    │ Page Header (LSN,    │ Key-Value Payload Slots      │ Slot Directory │
    │ Free Space Offset)   │ [Key 5, Value...] [Key 6...] │ (Offsets, grow │
    │                      │ <--- Free Space Area --->    │  backwards)    │
    └──────────────────────┴──────────────────────────────┴────────────────┘
```

---

### LSM-Trees: Out-of-Place Updates
Log-Structured Merge-Trees (LSM-Trees) are optimized for **write-heavy workloads**. Instead of overwriting existing records on disk, they perform **out-of-place updates**. All writes (inserts, updates, and deletes) are appended to a volatile in-memory structure called the **MemTable** and a sequential **Write-Ahead Log (WAL)** on disk for durability. 

When the MemTable fills up, it is flushed to disk as an immutable sorted file called a **Sorted String Table (SSTable)**. Background compaction threads continuously merge these SSTables to reclaim space.

```
                    LSM-TREE WRITE AND READ PATHWAYS
                    
        Write Path                                  Read Path
            │                                           │
            ├──> [ MemTable (RAM) ] <───────────────────┤ (1. Check MemTable)
            │      (SkipList/AVL)                       │
            v                                           v
      [ WAL (Disk) ]                            [ Bloom Filters (RAM) ]
    (Sequential Append)                         (2. Check to skip SSTables)
                                                        │
                                                        v
                                               [ SSTables L0 (Disk) ]
                                               [ SSTables L1 (Disk) ]
                                               [ SSTables L2 (Disk) ]
                                                        │
                                                        v
                                               [ Merged Result ]
```

---

## 2. Deep Dive

### Operation Mechanics: Step-by-Step Execution

#### Point Lookups (Search)
* **B-Trees**:
  1. Start at the Root Page in the Buffer Pool. If not cached, load it from disk.
  2. Perform a binary search on the Root Page's sorted keys using the Slot Directory to find the appropriate child page pointer.
  3. Traverse down to the internal pages, repeating binary search at each step.
  4. Reach the Leaf Page. Search the Slot Directory. If the key exists, return the value payload or reference pointer; else, return null.
  * **Complexity**: Exactly $\log_B N$ page reads (where $B$ is node fan-out, e.g., $B \approx 500$). With a height of 3 or 4, a B-Tree indexes billions of keys with minimal disk lookups.
* **LSM-Trees**:
  1. Search the active in-memory **MemTable**. If found, return it.
  2. Search any immutable, in-flight **MemTables** queued for flushing.
  3. Query the **$L_0$ SSTables** on disk. Because keys overlap in $L_0$, all $L_0$ files must be checked unless ruled out by Bloom Filters.
  4. Query the Leveled SSTables ($L_1, L_2, \dots$). For each level, keys do not overlap. The engine runs a binary search on the level's file-metadata to find the single candidate SSTable.
  5. Check the target SSTable's Bloom Filter. If it hits, read the index block to find the data block offset, fetch the data block, and read the key.
  * **Complexity**: Up to $O(L)$ disk lookups, where $L$ is the number of levels. Point read latency is naturally higher and more variable than in B-Trees.

#### Writes (Inserts, Updates, Deletes)
* **B-Trees**:
  1. Traverses the tree to find the correct Leaf Page.
  2. If the page is in the Buffer Pool, update it. If not, read it from disk into the pool, then update it.
  3. Mark the page as "dirty".
  4. Write the update to the database WAL (Redo Log) for durability.
  5. If the insert exceeds the page capacity, execute a **Page Split** (detailed below).
  6. Deletes are treated as updates that clear a slot directory pointer or merge empty pages.
* **LSM-Trees**:
  1. Append the operation (Insert, Update, Delete) to the Write-Ahead Log (WAL) on disk via sequential I/O.
  2. Write the record to the in-memory **MemTable** (SkipList).
  3. If the record is a delete, insert a **Tombstone** marker.
  4. When the MemTable hits its size limit (e.g., 64MB), transition it to read-only and flush it as a new $L_0$ SSTable in the background.
  * **Complexity**: $O(1)$ sequential disk I/O and RAM insert. Ingestion is extremely fast.

---

### B-Tree Deep Dive Internals

#### Algorithmic Page Traversal (Pseudo-code)
The following pseudo-code illustrates how a storage engine searches a B-Tree:

```go
func (tree *BTree) Find(key Key) (Value, error) {
    currentPageID := tree.RootPageID
    for {
        page := tree.BufferPool.GetPage(currentPageID)
        page.RLock() // Acquire shared latch
        
        if page.IsLeaf() {
            val, found := page.BinarySearchLeaf(key)
            page.RUnlock()
            if found {
                return val, nil
            }
            return nil, ErrKeyNotFound
        }
        
        // Internal page search to find pointer to next level
        nextPageID := page.BinarySearchInternal(key)
        page.RUnlock()
        currentPageID = nextPageID
    }
}
```

#### Page Directory and Slot Layout
A physical B-Tree page (usually 8KB to 16KB) is designed to handle variable-length keys without requiring constant memory shifts. It uses a **Slot Directory**:
* The page header tracks the free space boundaries.
* Key-value payloads are written left-to-right into the free space area.
* The slot directory is an array of 2-byte offsets placed at the very end of the page, growing right-to-left. Each slot points to the start of a key-value pair.
* To maintain sorted order, only the 2-byte offsets in the Slot Directory are rearranged, avoiding the overhead of shifting heavy payloads in memory.

#### Page Split Execution Path
When a write causes a page to overflow, the split occurs as follows:
1. **Allocate New Page**: The engine allocates a new page descriptor in the tablespace.
2. **Determine Split Point**: Find the median key in the overflowed page.
3. **Copy Data**: Copy all key-value entries greater than the median key to the new page.
4. **Clear Slots**: Erase the shifted entries from the original page and update its slot count.
5. **Adjust Pointers**: Update the `next` page pointer of the original page to point to the new page, and the `prev` pointer of the new page to point to the original.
6. **Propagate Upward**: Insert the median key and the pointer to the new page into the parent node. If parent overflows, recurse.

#### Node Overfill, Underfill, and Sibling Redistribution
* **Underfill (Underflow)**: When deletes reduce a leaf page's occupancy below a critical threshold (typically $50\%$ of maximum capacity), the engine does not immediately merge files.
* **Sibling Redistribution (Borrowing)**: The engine inspects the left and right sibling leaf pages. If a sibling has extra keys (occupancy $> 50\%$), the engine borrows keys from the sibling, updating the split parent key accordingly. This is much cheaper than merging nodes.
* **Node Merge**: If both siblings are also at the minimum threshold ($50\%$), the engine merges the underfilled node with a sibling and deletes the boundary key from the parent node.

#### Concurrency Control: Latch Crabbing (Coupling)
Because multiple threads read and write to the same tree, databases use latch crabbing to prevent structural corruption during splits/merges:
* **Read Queries**: Acquire Shared (S) Latch on Root -> Acquire S Latch on Child -> Release S Latch on Root. Repeat down the tree. Multiple readers traverse parallel paths.
* **Write Queries**: Acquire Exclusive (X) Latch on Root -> Acquire X Latch on Child.
  * If the child is "safe" (will not split because it has free slots), the writer can release the X Latch on the Root.
  * If the child is full, the parent X Latch is held because a split will propagate upward. The parent lock is only released after the child split finishes.

```
                          LATCH CRABBING (WRITER)
                          
  1. Lock Parent (X) ──────> [ Parent Node ] (Locked)
                                  │
                                  v
  2. Lock Child (X) ───────> [ Child Node  ] (Locked)
  
  3. Check Child safety: If Child has space, release Parent (X) lock.
```

#### Buffer Pool & Clock Sweep Eviction
To avoid hitting disk, the database manages page frames in a **Buffer Pool**.
When a page is requested, it is read into the pool. If the pool is full, pages are evicted via **Clock Sweep**:
* The engine keeps a pointer (hand) pointing to page descriptors in a circular array.
* Each page descriptor has a usage counter (or reference bit).
* The hand sweeps: if a page has a reference bit of 1, the hand resets it to 1 -> 0. If it is already 0, that page is selected for eviction.
* If the page is dirty, it is put into a flush queue to write back to disk before reuse.

#### Double-Write Buffer
On modern drives, writes happen in sectors (typically 512B to 4KB). If the system crashes while writing a database's 8KB page, the page becomes half-written (torn page), corrupting data.
* To prevent this, InnoDB uses a **Double-Write Buffer**.
* Dirty pages are first written to a contiguous layout in the Double-Write Buffer on disk and fsynced.
* Only after this completes does the engine write the page to its actual table space.
* If a crash occurs during the tablespace write, the engine recovers the intact page from the Double-Write Buffer.

---

### LSM-Tree Deep Dive Internals

#### MemTable Concurrent SkipList
A SkipList consists of a base linked list of keys, with probabilistic higher levels that skip over nodes.
* **Concurrency**: Modern LSMs use Lock-Free Concurrent SkipLists (implemented via Atomic Compare-And-Swap (CAS) pointers) to allow concurrent writers to insert keys without locking the entire index.

```
                         SKIPLIST LEVEL TRAVERSAL
                         
  Level 3: [Head] ───────────────────────────> [30] ──────────────────────> [Null]
                                                │
  Level 2: [Head] ─────────────> [15] ────────> [30] ───────────> [70] ───> [Null]
                                  │             │                 │
  Level 1: [Head] ──> [05] ─────> [15] ──> [22] ──> [30] ──> [45] ──> [70] ───> [Null]
```

#### SSTable Sparse Index and Block Layout
An SSTable contains:
* **Data Blocks**: Containing keys sorted and delta-encoded (e.g., instead of storing "database" and "datastore", it stores "database" and "4,store" to save space).
* **Sparse Index**: Instead of indexing every key, it indexes only the first key of each block (e.g., 1 index entry per 4KB data block). The sparse index is kept in RAM.
* **Bloom Filter**: Uses a bit-array of size $m$. For each key, it calculates $k$ hash values, setting those bit offsets to 1. If any of the $k$ bits are 0 for a query key, the key is guaranteed not to exist in that SSTable.

```
                      SSTABLE PHYSICAL DISK LAYOUT
 ┌──────────────────────┬──────────────────────┬──────────────┬────────────┐
 │ Data Block 1         │ Data Block 2         │ Bloom Filter │ Index      │
 │ [Key-Val 1] [K-V 2]  │ [Key-Val 3] [K-V 4]  │ Bit-Array    │ Block      │
 └──────────────────────┴──────────────────────┴──────────────┴────────────┘
                                                               ▲ Offset pointers
```

#### LSM-Tree Read Optimization: Caching Layers
Because lookups can span multiple SSTables, LSMs implement hierarchical caching:
1. **Row Cache**: Stores fully uncompressed and deserialized row objects in RAM. If hit, it bypasses the entire LSM search path.
2. **Block Cache**: Stores uncompressed SSTable data blocks. Bypasses disk reads and decompression.
3. **OS Page Cache**: Caches the raw, compressed files on disk. Managed by the operating system.
4. **Key-Prefix Bloom Filters**: Used for range queries. If the application queries keys with prefix `user_101_*`, the prefix Bloom filter evaluates if that prefix group exists in the SSTable.

#### Compaction Deep Dive

##### Size-Tiered Compaction Strategy (STCS)
STCS groups files of similar sizes (e.g., four 100MB files) and merges them into one 400MB file.
* **Pros**: Simple, low CPU write amplification on ingestion.
* **Cons**: Severe space amplification. During a merge of a 10GB dataset, the engine must write a new 10GB file before deleting the old ones. The disk must keep $50\%$ of its space free to support compaction.

##### Leveled Compaction Strategy (LCS)
LCS maintains non-overlapping keys in Levels.
* **Process**:
  1. $L_0$ files can overlap. When $L_0$ reaches 4 files, they are merged with overlapping files in $L_1$.
  2. For $L_1$ and above, keys do not overlap.
  3. If $L_1$ exceeds 10MB, the engine picks one file from $L_1$ (e.g. range key "D-H") and merges it with all files in $L_2$ that overlap with "D-H" (e.g. files "C-E" and "F-I").
* **Pros**: Keeps space amplification low ($< 10\%$). Point reads are fast because a key is present in at most one SSTable per level.

```
  L1:  [  Key: A - D  ]   [  Key: E - H  ]   [  Key: I - L  ]   (Non-overlapping)
             \                  /
              v                v
  L2:  [ Key: A - C ]  [ Key: D - F ]  [ Key: G - I ]  [ Key: J - L ] (Non-overlapping)
```

#### Crash Recovery Metadata: The MANIFEST File
Because SSTable configurations change dynamically with flushes and compactions, LSM-trees maintain a **MANIFEST** file on disk. This file tracks the active SSTables, their levels, and key ranges. During startup, the engine reads the MANIFEST to reconstruct the database's structural tree state.

---

## 3. Amplification Factors: Math & Calculations

### Write Amplification Factor (WAF)

$$\text{WAF} = \frac{\text{Bytes Written to Disk}}{\text{Logical Bytes Written by Client}}$$

#### B-Tree WAF Calculation
Suppose we update a 128-byte row.
* Standard Page Size: 8192 bytes (8KB).
* Execution path:
  1. Write to WAL (128 bytes).
  2. Write to Double-Write Buffer (8192 bytes).
  3. Write Page to Tablespace (8192 bytes).
* **Total Written**: $128 + 8192 + 8192 = 16512$ bytes.
* **WAF**:
  $$\text{WAF} = \frac{16512}{128} \approx 129$$

#### LSM-Tree WAF Calculation
In Leveled Compaction with a multiplier $T = 10$, a key is read and rewritten up to 10 times at each level it travels down.
* If a database has 4 levels, the compaction WAF is roughly $10 \times 4 = 40$.
* Although compaction WAF is high, these writes are batch-processed and written sequentially, bypassing the random write degradation of SSDs.

---

### Read Amplification Factor (RAF)

$$\text{RAF} = \frac{\text{Bytes Read from Disk}}{\text{Logical Bytes Requested by Client}}$$

#### Point Lookup RAF
* **B-Tree**: To read a 128-byte record, the engine reads exactly one 8KB page.
  $$\text{RAF} = \frac{8192}{128} \approx 64$$
* **LSM-Tree**: If Bloom filters fail and the engine has to read 4 separate SSTable pages to find the latest version of a key:
  $$\text{RAF} = \frac{4 \times 4096}{128} \approx 128$$

---

### Space Amplification Factor (SAF)

$$\text{SAF} = \frac{\text{Physical Storage Size}}{\text{Logical Size of Clean Dataset}}$$

* **B-Tree**: Due to page fragmentation, leaf pages are often only $60 - 70\%$ full.
  $$\text{SAF} \approx \frac{1}{0.67} \approx 1.5$$
* **LSM-Tree (STCS)**: Because stale values coexist until a full compaction, SAF can reach $2.0 - 3.0$.
* **LSM-Tree (LCS)**: Deletes and duplicates are purged quickly.
  $$\text{SAF} \approx 1.1 - 1.2$$

---

## 4. Multi-Version Concurrency Control (MVCC) Implementation

### MVCC in B-Trees
B-Trees perform MVCC by avoiding direct overwrites of active rows when a transaction holds an older view of the database.
* **InnoDB Undo Logs**: When a row is updated, InnoDB writes the old version of the row data to an **Undo Log** segment and points the active page's row header to the Undo Log offset.
* **PostgreSQL MVCC**: Postgres writes a new tuple directly into the table heap page (or another page if the original page is full) with metadata columns:
  * `xmin`: The transaction ID that created the tuple.
  * `xmax`: The transaction ID that deleted or replaced the tuple.
* **Cleanup (Vacuuming/Purging)**: A background worker (e.g. Postgres Autovacuum) must scan heap pages to find and reclaim physical space occupied by dead tuples (tuples where `xmax` is older than the oldest active transaction). This causes heavy read/write cycles.

### MVCC in LSM-Trees
LSM-Trees implement MVCC inherently due to their append-only structure.
* **Sequence Numbers**: Every entry in a MemTable/SSTable is tagged with an incremental **Sequence Number** (or timestamp).
* **Updates/Deletes**: An update simply writes a key-value record with a higher sequence number. A delete writes a Tombstone record.
* **Compaction Purging**: During compaction, the merge-iterator detects duplicate keys across levels. It preserves only the record with the highest sequence number that is older than the oldest active snapshot transaction. All older versions and tombstones are discarded, making MVCC cleanup an automatic byproduct of background disk organization.

---

## 5. SSD Wear-Out and Physical Endurance

SSD storage degrades due to physical limits on NAND flash cell program-erase (P/E) cycles. 
* **P/E Cycles**: Depending on the technology (SLC vs TLC/QLC), cells can tolerate between 1,000 and 100,000 write cycles before wearing out.
* **FTL Garbage Collection**: Write amplification at the SSD controller level is triggered when random writes scatter updates across different blocks. The SSD must read the block, erase it, and rewrite it.
* **B-Tree Impact**: B-Trees write random pages, forcing the FTL to continuously perform block erasures. This degrades SSD lifespan rapidly under high-throughput workloads.
* **LSM-Tree Impact**: LSM-Trees write in large, contiguous blocks matching the SSD block size. This allows the SSD controller to bypass garbage collection overhead, preserving SSD lifespan.

---

## 6. Complete Trade-off Matrix

| Feature | B-Tree Storage Engines | LSM-Tree Storage Engines |
| :--- | :--- | :--- |
| **Write Throughput** | Low (bounded by random I/O and lock contention). | High (sequential appends to WAL and MemTable). |
| **Point Lookup Latency** | Low and Predictable ($O(\log N)$ tree reads). | Variable (May search multiple SSTable files). |
| **Range Query Speed** | Fast (Traverses sorted leaf pointers). | Slow (Must merge scans across multiple SSTables). |
| **SSD Durability (Wear)** | Bad (Random page writes degrade SSD cells). | Good (Sequential blocks match SSD write patterns). |
| **Memory Consumption** | Low-to-Medium (Primarily page cache). | High (Requires Bloom filters, Sparse indexes in RAM). |
| **Write Stalls / Latency Jitter**| Minimal (Predictable background page cleaning).| Severe (Occurs when compaction lags behind ingestion). |
| **Storage Fragmentation** | High (Unused space within pages due to splits). | Low (SSTables are densely packed and compressed). |
| **Memory Mapped File Support** | Yes (e.g. LMDB uses mmap direct mappings). | No (Requires explicit block caches and buffers). |
| **Transaction Isolation** | High concurrency lock trees are simple. | Medium complexity (Requires MVCC version tracking). |
| **Recovery Speed** | Fast (Replay small WAL, check page LSNs). | Slow (Must replay WAL and rebuild MemTables from scratch).|
| **Bloom Filters Required** | No | Yes |
| **Compression Ratio** | Low (8KB aligned block limitations). | High (SSTables are immutable and highly compressed). |
| **Delete Performance** | Moderate (Triggers leaf slot modifications). | Fast (Writes small tombstone marker instantly). |
| **Secondary Indexes** | Simple (Pointer to Primary Clustered Key). | High Overheads (Incurs double index scans). |
| **Sequential Write Alignment**| Low | High |

---

## 7. Real-world Usage

### B-Tree Databases

#### PostgreSQL
* **Engine Type**: Heap storage files accompanied by B-Tree Indexes.
* **Tuning Details**: PostgreSQL uses B-Trees for primary and secondary indexes. The tables themselves are unordered heaps. High write volumes lead to index fragmentation. Tuning `fillfactor` below $100\%$ allocates free space in pages, reducing split frequency on updates.

#### MySQL InnoDB
* **Engine Type**: Clustered $B^+$-Tree Index.
* **Tuning Details**: Data is stored inside the primary key index leaf pages. Secondary indexes point to the primary key, incurring a double-lookup penalty. Tuning `innodb_buffer_pool_size` is critical, with recommended values of $70-80\%$ of system RAM to cache hot leaf nodes.

---

### LSM-Tree Databases

#### Cassandra / ScyllaDB
* **Engine Type**: LSM-Tree.
* **Tuning Details**: Designed for high-volume write workloads. Uses Size-Tiered Compaction (STCS) for write-heavy pipelines and Leveled Compaction (LCS) for read-heavy query tables. Bloom filters are tuned using the `bloom_filter_fp_chance` parameter.

#### RocksDB
* **Engine Type**: Leveled LSM-Tree.
* **Tuning Details**: Embedded engine used by CockroachDB and TiDB. Features key prefix-blooms to speed up range searches, block cache sizing, and parallel background compaction threads (`max_background_compactions`).

---

## 8. SDE-2 Interview Script

### Scenario
An interviewer asks the candidate to design the database layer for an IoT system capturing thousands of telemetry events per second.

### Playbook Dialogue

* **Interviewer**: "We are designing a telemetry storage platform for connected vehicles. We receive 300,000 status updates per second. We need to query the latest coordinates of a vehicle, and run hourly range scans to analyze vehicle speed. Which database engine architecture would you recommend?"

* **Candidate (Junior Answer)**: "I would use a relational database like PostgreSQL. We can create an index on the vehicle ID and the timestamp. Since B-Trees support fast queries, reading coordinates and doing range scans will be simple. If the database gets slow, we can just add write replicas or shard the tables by vehicle ID."

* **Interviewer**: "At 300,000 writes per second, sharding a B-Tree database will be expensive. B-Trees execute in-place updates, which triggers random page writes, page splits, and write amplification. This can saturate disk bandwidth and wear out SSDs quickly. How can we optimize the write path?"

* **Candidate (Senior Answer)**: "For this workload, we should use an **LSM-Tree-based storage engine** (like Cassandra or RocksDB/Pebble) rather than a B-Tree engine.
  
  In an LSM-Tree, incoming telemetry writes are appended sequentially to a **Write-Ahead Log (WAL)** for durability, and written to an in-memory **MemTable**. This turns random write requests into sequential disk operations, bypassing B-Tree page split overhead.
  
  When the MemTable fills up, it is flushed to disk as a sorted, immutable **SSTable**. We can use **Bloom Filters** loaded in RAM to bypass SSTables that do not contain the target vehicle's keys, ensuring point lookups remain fast. For hourly range queries, because SSTables are internally sorted, we can execute sorted scans and merge them. This handles the high write volume efficiently while keeping reads performant."

* **Interviewer**: "What about the trade-offs? LSM-Trees can suffer from write stalls. What causes this, and how would you resolve it?"

* **Candidate (Senior Answer)**: "Write stalls happen when background compaction lags behind the rate of incoming MemTable flushes. If $L_0$ accumulates too many overlapping SSTables, the engine stalls writes to prevent disk space exhaustion and high read amplification.
  
  To mitigate this:
  1. We can tune compaction threads to match write throughput.
  2. We can use a **Leveled Compaction Strategy** to bound space amplification and keep the number of overlapping files small.
  3. We can allocate a larger memory buffer for MemTables, allowing the engine to batch writes into larger SSTable flushes."

* **Interviewer**: "Can you walk me through the detailed difference between Leveled Compaction and Size-Tiered Compaction? Which is better for SSD wear?"

* **Candidate (Senior Answer)**: "Size-Tiered Compaction writes larger files in a single pass. It requires fewer overall write cycles initially, which minimizes SSD wear compared to Leveled Compaction. However, Leveled Compaction rewrites pages multiple times to maintain non-overlapping ranges, resulting in higher Write Amplification (WAF), which wears out SSD cells faster. But Leveled Compaction is much more space-efficient, offering Space Amplification (SAF) values close to 1.1, compared to Size-Tiered which often requires 50% free disk overhead."

---

## 9. SDE-2+ Readiness Checklist

- [ ] I can explain the physical storage layout differences between random and sequential I/O.
- [ ] I understand the internal layout of a B-Tree page (Page Header, Key Slots, and Slot Directory).
- [ ] I can describe the mechanics of B-Tree page splits and how they propagate up the tree.
- [ ] I understand B-Tree concurrency control using latch crabbing protocols.
- [ ] I can outline the full write path of an LSM-Tree (MemTable, WAL, $L_0$ to $L_k$ SSTables).
- [ ] I know how Bloom filters use bit-arrays and hashing to prevent redundant disk reads in LSM-Trees.
- [ ] I can compare Size-Tiered Compaction Strategy (STCS) and Leveled Compaction Strategy (LCS).
- [ ] I can define and explain Read, Write, and Space Amplification.
- [ ] I can identify when to recommend B-Trees (low read latency, OLTP transactional integrity) vs LSM-Trees (high write volumes, telemetry).
- [ ] I know how to tune storage engine parameters (buffer pool size, block size, compaction threads) to optimize performance.
- [ ] I understand the purpose and protection provided by the Double-Write Buffer in B-Tree systems.
- [ ] I can explain the role of the MANIFEST file in LSM-Tree recovery.
- [ ] I understand how MVCC is handled in B-Trees via Undo logs/Vacuuming versus LSM-Trees via Sequence Numbers during Compaction.
- [ ] I can analyze secondary index impacts on LSM-trees and how they degrade read amplification.
- [ ] I understand physical SSD wear characteristics (P/E cycles, flash write amplification) and how storage engine designs impact hardware longevity.
- [ ] I can describe the difference between physical page layout formats (e.g., slotted-page vs. fixed-record format) and their query scanning performance.
- [ ] I understand how the operating system's page cache interacts with database buffer pools and the implications of double buffering.
- [ ] I can explain what a torn page is and how different database engines protect against it (e.g., MySQL's Double-Write Buffer vs. PostgreSQL's Full Page Writes).


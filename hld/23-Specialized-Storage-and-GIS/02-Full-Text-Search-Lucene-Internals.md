# ⚡ 02 - Full-Text Search (Lucene Internals)

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C111 |
| **Category** | Specialized Storage & GIS |
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
*   **Two-Sentence Trigger:** Full-text search engines like Elasticsearch and Solr are built on top of Apache Lucene, which processes unstructured text into an inverted index mapping tokens to their corresponding document IDs. System architects choose this engine when designing search-heavy applications (e.g., e-commerce product search, log analytics engines like Datadog/Kibana) requiring fuzzy matching, prefix completion, and relevance ranking (BM25) over vast volumes of unstructured text.
*   **Scalability Dimension:** Primary: **Read Query Latency** (for complex boolean, wildcard, and phrase searches) and **Heap Memory Footprint** (for storing the Term Index in RAM). Secondary: **Ingestion Latency** and **Write Amplification** (caused by frequent segment creation and background merges).

---

## ⚖️ 2. Trade-offs & Deep Dive

### The Analysis Pipeline
Before text enters the index, it undergoes a transformation pipeline:
1.  **Character Filters:** Cleans raw text (e.g., stripping HTML tags `<b>` -> text).
2.  **Tokenizer:** Splits the text stream into individual tokens (e.g., whitespace tokenizer splits "search engine" to `["search", "engine"]`).
3.  **Token Filters:** Modifies tokens (e.g., Lowercasing, Stemming `["running" -> "run"]`, Stop-words removal `["the", "and"]`, and Synonyms expansion `["quick" -> "fast"]`).

### Lucene Index Architecture

```
Search Query: "fast cars"
      │
      ▼
  [Analyzer] ──► Tokens: ["fast", "car"]
      │
      ▼
┌────────────────── JVM Heap (In-Memory) ──────────────────┐
│ Term Index (FST - Finite State Transducer)               │
│  - Traverses prefixes: "f-a-s-t" -> Points to Disk Block  │
└──────────────────────────┬───────────────────────────────┘
                           │ (Direct Seek to Block Offset)
                           ▼
┌─────────────────────── Disk ─────────────────────────────┐
│ Term Dictionary (Sorted Term Blocks)                     │
│  - "fast" ──────────────────────────────┐                │
│  - "car"  ──────────────┐               │                │
├─────────────────────────┼───────────────┼────────────────┤
│ Posting Lists           ▼               ▼                │
│  - [Term: "car"]  ──► Doc#2, Doc#5, Doc#12               │
│  - [Term: "fast"] ──► Doc#1, Doc#5, Doc#9                │
└──────────────────────────────────────────────────────────┘
```

1.  **Term Index (FST):** 
    Stored entirely in JVM memory. Lucene uses a **Finite State Transducer (FST)**—a compressed directed acyclic graph (similar to a Trie but sharing both prefixes and suffixes)—to map term prefixes to byte offsets in the Term Dictionary on disk. This avoids keeping all unique terms in RAM.
2.  **Term Dictionary:** 
    A sorted file stored on disk containing all unique terms and statistics (document frequency). The FST allows the engine to jump directly to the target term's block on disk without doing a binary search.
3.  **Posting List:** 
    For each term, Lucene maintains a list of document IDs where the term appears, alongside term frequency, position offsets (for phrase queries), and payloads.
    *   *Posting List Compression:* To minimize disk footprint, Lucene compresses posting lists using **Frame of Reference (FOR)** (delta-encoding values and packing them into bit-aligned arrays) or **Roaring Bitmaps** (dynamic encoding based on integer density).

### Ingestion Lifecycle: Lucene Segments
*   **Immutability:** Lucene writes data in immutable units called **Segments**.
*   **Write Flow:**
    1.  Incoming documents are written to an in-memory **Index Buffer** and also appended to a transaction log (translog) for durability.
    2.  During a **Refresh** (typically every 1 second), the buffer is written to the OS filesystem cache as a new Segment, making the document searchable (near real-time search).
    3.  During a **Flush** (or commit), the segments are written and synced (`fsync`) to persistent storage.
*   **Segment Merges (TieredMergePolicy):** 
    Because segments are immutable, documents cannot be updated in place. Updates are written as a logical delete (marking the ID in a `.del` bitmap) and a new insertion. A background merge thread reads multiple small segments and compiles them into a single larger segment, permanently purging the deleted records.

### Comparison Table

| Feature | Inverted Index (Lucene) | B-Tree (RDBMS Index) | Columnar Index (Doc Values) |
| :--- | :--- | :--- | :--- |
| **Primary Use Case** | Text matching and search queries | Exact match and range queries | Sorting, aggregations, and metrics |
| **Data Layout** | Term -> List of Doc IDs (Row-like list) | Tree nodes pointing to table pages | Doc ID -> Values (Columnar array) |
| **Relevance Scoring** | BM25 / TF-IDF | None (Binary inclusion/exclusion) | None |
| **Write Performance** | High throughput (buffered memory writes) | Poor for massive scales (lock contention) | Medium (built alongside inverted index) |
| **Storage Overhead** | Large (FST in Heap, Posting Lists on Disk) | Medium (Compact indices) | Low (Highly compressible column data) |

---

## 💥 3. Resiliency & Operations

*   **Observability (The "Signal"):**
    *   `Segment Count per Shard`: A high segment count (e.g., > 50) indicates the merging process is lagging behind ingestion, slowing down reads because the engine must query every segment individually.
    *   `Index Refresh Latency / Merge Throttling`: Monitor disk I/O saturation. Frequent small updates trigger excessive segment writes, saturating disk write IOPS.
    *   `JVM Garbage Collection (GC) Pauses`: Large heap allocations for FST caching can cause long Stop-The-World (STW) GC pauses.
*   **Blast Radius (The "Impact"):**
    *   Wildcard search operations beginning with a wildcard (e.g., `*term`) force Lucene to scan the entire Term Dictionary, bypassing the FST prefix optimizations, causing CPU starvation and thread pool exhaustion.
    *   *Mitigation:* Configure query limits (e.g., disable leading wildcards in search properties), implement search query timeouts, and route heavy aggregation/sorting queries to use **Doc Values** instead of Fielddata.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   **Updating Documents in Place:** Assuming Lucene updates documents in place like an RDBMS. In reality, it runs a delete-and-reinsert cycle, which incurs write amplification and requires background segment merges.
*   **Aggregation on Analyzed Fields:** Trying to perform aggregations on fields indexed with a full text analyzer without realizing that it loads all terms into the JVM heap (Fielddata), leading to GC thrashing and OOMs.
*   **Assuming Absolute Real-time:** Believing Elasticsearch is immediate (ACID). It is Near Real-Time (NRT) due to the decoupling of Memory Index Buffers and Segment Refreshes.

### Interview Tip (The "Strong Hire" Signal)
> "When designing an e-commerce search service, I prevent JVM heap exhaustion by disabling JVM-level Fielddata for sorting and aggregations, enforcing the use of Doc Values (Lucene's columnar format written to disk during indexing and accessed via OS page cache). To optimize write-intensive ingestion, I increase the Elasticsearch refresh interval from 1s to 30s and disable replica shards during initial bulk imports, drastically reducing CPU cycles wasted on micro-segment merges."

---

## 💡 5. My Custom Study Notes & Whiteboard

### Posting List Compression: Frame of Reference (FOR)
Suppose we have a Posting List of Document IDs: `[103, 107, 115, 120]`.
Instead of storing these 32-bit integers directly, Lucene applies delta-encoding:
1.  **Delta Encoding:** Calculate differences:
    *   `103` (Base)
    *   `107 - 103 = 4`
    *   `115 - 107 = 8`
    *   `120 - 115 = 5`
    *   Deltas: `[103, 4, 8, 5]`
2.  **Bit Packing:** 
    *   Identify the maximum delta: `103` (requires 7 bits).
    *   Store all deltas using exactly 7 bits per integer instead of the standard 32 bits, saving up to 78% storage.

### BM25 Ranking Formula (Relevance Scoring)
Unlike TF-IDF, which allows score to increase infinitely with term frequency, BM25 dampens the score of repeated terms:

$$Score(D, Q) = \sum_{i=1}^{n} IDF(q_i) \cdot \frac{f(q_i, D) \cdot (k_1 + 1)}{f(q_i, D) + k_1 \cdot \left(1 - b + b \cdot \frac{|D|}{avgdl}\right)}$$

*   $f(q_i, D)$: Term frequency of term $q_i$ in document $D$.
*   $k_1$: Controls term frequency saturation (typically $1.2$ to $2.0$). High value means frequency matters longer.
*   $b$: Controls document length normalization (typically $0.75$). Penalizes long documents containing the term just by chance.
*   $avgdl$: Average document length across the collection.

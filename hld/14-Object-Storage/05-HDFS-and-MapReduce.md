# ⚡ 05 - HDFS & MapReduce

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C109 |
| **Category** | Object Storage & Big Data |
| **Difficulty** | 🟡 Medium |
| **Interview Frequency** | 🟡 Medium |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** HDFS (Hadoop Distributed File System) is a distributed, block-structured file system designed to run on clusters of commodity hardware, while MapReduce is the companion programming framework that processes these massive datasets in parallel. It is triggered when designing batch data processing systems that require running computations directly on the physical nodes where raw data blocks reside (Data Locality) to bypass high network transfer costs.
*   **Scalability Dimension:** Primary: **Data Locality Compute Scheduling vs. Network Shuffle Aggregation Overhead**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### HDFS Topology & MapReduce Execution Flow
HDFS splits large files into blocks (typically 128MB) and replicates them across DataNodes:
```
HDFS Storage Layer:
  [ NameNode (Master) ]  ◄── Holds file metadata & block mappings in RAM (SPOF)
          │
  ┌───────┼───────┐
  ▼       ▼       ▼
[ DataNode 1 ] [ DataNode 2 ] [ DataNode 3 ]  ◄── Stores raw blocks (Default: 3x replication)

MapReduce Compute Layer:
  [ Data block 1 ] ──► [ Map Phase ] ──┐
  [ Data block 2 ] ──► [ Map Phase ] ──┼──► [ Shuffle & Sort ] ──► [ Reduce Phase ] ──► Output (HDFS)
  (Runs locally on same DataNode)      │     (Network Heavy)       (Aggregates values)
                                       ▼
                             [ Intermediary Disk ]
```

### The Three MapReduce Stages
1. **Map Stage:** Reads input file blocks locally on each DataNode. Runs user code to transform rows into key-value pairs `(key, value)`.
2. **Shuffle Stage (The Bottleneck):** Transfers, sorts, and groups all key-value pairs by key across the network. All values of key "A" must end up on the same reducer node. This is a heavy network I/O phase.
3. **Reduce Stage:** Aggregates the grouped values for each unique key and writes the resulting output back to HDFS.

---

### HDFS vs. AWS S3 Storage Trade-offs
| Dimension | HDFS (Distributed File System) | AWS S3 (Cloud Object Storage) |
| :--- | :--- | :--- |
| **Data Mutability** | Supports appends. No random writes. | Immutable (writes overwrite the entire object). |
| **Operational Overhead**| 🔴 High. Requires managing servers, disks, and NameNodes. | 🟢 Zero. Fully managed, serverless. |
| **Compute Integration** | 🟢 High (supports Data Locality - runs code on storage). | 🔴 Low (requires transferring data over network to compute). |
| **Scale Limitations** | 🔴 NameNode RAM limits total number of files. | 🟢 Virtually infinite scaling. |
| **Cost** | 🔴 Expensive (runs active compute nodes). | 🟢 Cheap (pay only for gigabytes stored). |

---

## 💥 3. Resiliency & Operations

### Operational Pitfalls & Mitigations
*   **The Small File Problem (NameNode Exhaustion):**
    *   *Problem:* Storing millions of small files (e.g., 10KB images) in HDFS. The NameNode keeps metadata for every file and block in its local RAM. Billions of small files exhaust the NameNode's JVM Heap, crashing the entire cluster and rendering petabytes of DataNode blocks unreadable.
    *   *Mitigation:* Merge small files using Hadoop Archives (HAR) or convert logs to larger sequential block formats (SequenceFiles, Parquet) before uploading to HDFS.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Suggesting HDFS/MapReduce as a modern design for low-latency dashboard queries. MapReduce is a high-latency batch processing framework. Recommend Spark (which runs in-memory and is 100x faster) or columnar warehouses.
*   Not mentioning the NameNode single point of failure (SPOF) and RAM limitation.

### Interview Tip (The "Strong Hire" Signal)
> *"For our legacy batch logging system, we use HDFS to achieve high write throughput. We replicate our raw blocks 3x across DataNodes to survive disk failures. To prevent the HDFS Small File Problem from exhausting our NameNode's RAM, we combine our raw logs into 128MB Parquet files. When running MapReduce jobs, we schedule mapper tasks directly on the DataNodes holding the targets to exploit Data Locality, minimizing our network card egress bottleneck during the Map phase."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

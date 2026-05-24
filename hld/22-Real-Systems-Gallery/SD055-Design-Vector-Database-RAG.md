# 🏢 SD055 - Design a Vector Database / RAG Search Engine

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Problem ID** | SD055 |
| **Category** | AI Infrastructure |
| **Difficulty** | 🔴 Expert |
| **Interview Frequency** | 🔥 Must Know (2024–2026) |
| **Target Companies** | Pinecone, OpenAI, Cohere, Elastic, Snowflake |
| **SDE-2 Mandatory** | ✅ Yes |
| **Status** | Completed |
| **Times Practiced** | 1 |
| **Last Practiced** | 2026-05-24 |
| **Next Review** | 2026-06-24 |
| **Confidence** | 🟢 Applied |
| **Mastery** | 🟢 Expert |

---

## 📋 1. Core Requirements & Scale

### Functional Requirements
- Ingest and chunk raw documents, compute high-dimensional embeddings, and index them.
- Support low-latency approximate nearest neighbor (ANN) search based on query embeddings.
- Enable metadata filtering (e.g., filter results where `tenant_id = 5` or `created_year >= 2025`).
- Keep index updated in real-time as documents are added or updated.

### Non-Functional Requirements
- **Low Query Latency**: P99 search execution < 50ms for 1B vectors.
- **High Recall Accuracy**: Match closest items with > 95% accuracy compared to exact KNN.
- **Data Isolation**: Strict multi-tenant security filters.
- **Index Rebuild Elasticity**: Avoid query blocks or memory spikes during background indexing.

### Scale Targets (Back-of-the-Envelope)
- **Dataset Size**: 1 Billion vectors.
- **Dimensions**: 1536 dimensions per vector (standard OpenAI embedding size).
- **Storage Size**: $1,000,000,000 \times 1536 \times 4 \text{ bytes (float32)} \approx 6.14 \text{ TB}$ raw vector storage (excluding graph indexes).
- **Index memory footprint**: HNSW indexes can require 1.5x to 2x the raw vector size, requiring sharding across multiple nodes.

---

## 📐 2. High-Level Architecture

```
                    [ Document Ingestion Gate ]
                                │
                                ▼
         [ Chunking & Parser ] ──► [ Embedding Generator API ]
                                              │
                                              ▼
                             [ Write / Ingestion Coordinator ]
                                              │
                    ┌─────────────────────────┴────────────────────────┐
                    ▼                                                  ▼
      [ Index Builder Node (HNSW) ]                      [ Metadata Store (PostgreSQL) ]
                    │                                                  │
                    ▼                                                  ▼
     [ Vector Shard Nodes (SSD/RAM) ]                     [ Metadata Indexes ]
                    ▲                                                  ▲
                    └─────────────────────────┬────────────────────────┘
                                              │ (Filtered ANN Search Join)
                                              ▼
                                    [ Query Coordinator ]
                                              ▲
                                              │ (User Semantic Prompt Query)
                                              ▼
                                    [ Load Balancer ]
```

---

## ⚖️ 3. Deep Dive & Core Components

### A. Approximate Nearest Neighbor (ANN) Indexing: HNSW vs. IVF
Performing exact K-Nearest Neighbor (KNN) requires calculating distance against every vector in the DB, which is $O(N)$ and completely unscalable. We use ANN indexing:
* **HNSW (Hierarchical Navigable Small World)**:
  * **How it works**: Builds a multi-layer graph where the top layers have long-range links (fast navigation) and the bottom layers have short-range links (precise local search). Similar to a skip-list.
  * **Trade-off**: Extremely fast search ($O(\log N)$) and high recall, but has very high memory consumption (graphs stored in RAM) and slow index construction.
* **IVF (Inverted File Index)**:
  * **How it works**: Clusters the vector space using K-Means and maps cluster centroids. During search, it compares query embeddings only against vectors in the closest clusters.
  * **Trade-off**: Very low memory footprint and fast index build, but lower recall accuracy than HNSW.

### B. Metadata Filtering: Pre-Filtering vs. Post-Filtering
Real-world queries combine semantic search with strict SQL-like rules (e.g., "Find docs about LLD, but only for `user_id = 9`"):
* **Post-Filtering**: Perform the ANN vector search first, retrieve top 100 results, then filter out items that don't match metadata.
  * **Problem**: If the metadata filter is highly restrictive, you may end up with 0 results (known as "recall collapse").
* **Pre-Filtering**: Filter the metadata index first, and then perform ANN search *only* on the matching subset.
  * **Problem**: Can be extremely slow if the metadata query matches millions of documents, as it renders the HNSW graph traversal useless.
* **Senior Solution (Single-Stage Filtering)**: Custom engines (like Pinecone or pgvector with HNSW) traverse the HNSW graph and evaluate metadata criteria at *each node step* of the graph traversal, rejecting invalid nodes dynamically.

---

## 🚫 4. Common Mistakes & Interview Playbook

### Common Mistakes (The "Junior" Signals)
- Recommending brute-force cosine similarity checks on relational database tables under a 1B vector scale.
- Forgetting that graph indexes like HNSW need to reside in RAM to perform quickly, failing to describe horizontal database sharding.
- Proposing post-filtering for metadata queries without recognizing the risk of recall collapse.

### Interview Tip (The "Strong Hire" Signal)
> *"For a dataset of 1B vectors with 1536 dimensions, the raw vectors plus the HNSW index graphs will exceed 12 TB. We shard this dataset horizontally across nodes using consistent hashing on the tenant ID. We implement single-stage filtering, where metadata constraints are evaluated inline during the graph traversal to avoid both recall collapse and graph navigation failures."*

# ⚡ 01 - S3 & Object Storage

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C105 |
| **Category** | Object Storage & Big Data |
| **Difficulty** | 🟢 Easy |
| **Interview Frequency** | 🔥 High |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Object Storage is a flat, non-hierarchical data storage architecture that manages data as distinct, metadata-tagged units called objects rather than block-level files inside a folder hierarchy. It is triggered when storing massive volumes of unstructured or semi-structured files (such as user avatars, videos, database backups, and logs) that require near-infinite horizontal scaling and cost-efficiency at the expense of random write operations.
*   **Scalability Dimension:** Primary: **Metadata Control Plane vs. Bulk Binary Data Plane Isolation**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Object Storage Architecture Flow
Object storage decouples metadata queries from raw byte transfers to prevent application servers from becoming bottlenecks:
```
  [ Client Browser ] ─── 1. POST: Request Upload URL ───► [ API Gateway / App Server ]
                                                                 │
                                                        (Authorize & Sign URL)
                                                                 ▼
  [ Client Browser ] ◄─── 2. HTTP 200: Pre-signed URL ◄──────────┘
         │
         └────────────── 3. PUT: Direct file upload ─────────────► [ S3 Data Plane ]
                                                               (Bypasses App Server)
```

### Key Technical Operations
1. **Pre-signed URLs (Save Bandwidth):**
   * *Traditional approach:* Client uploads file to App Server; App Server uploads file to S3. This consumes double the egress bandwidth and CPU on the App Server.
   * *Pre-signed approach:* The App Server generates a cryptographic temporary upload link (authenticated via IAM credentials) and returns it to the client. The client uploads the binary payload **directly to S3**, completely offloading the app server fleet.
2. **Multipart Uploads (Reliability):**
   * Splitting large files (> 100MB) into smaller chunks (parts) and uploading them in parallel.
   * *Resiliency:* If network connectivity drops while uploading part 45 of a 5GB video, only part 45 is retried—not the entire 5GB.
3. **Prefix S3 Partitioning (Throughput Optimization):**
   * S3 partitions scale throughput per directory path (prefix) to support:
     * **3,500 PUT/POST/DELETE requests per second.**
     * **5,500 GET requests per second.**
   * *The Bottleneck:* Storing all files under a single flat folder (e.g., `s3://my-bucket/uploads/file1.jpg`) triggers partition hotspots, resulting in `HTTP 503 SlowDown` errors.
   * *The Solution:* Inject dynamic hash prefixes (e.g., `s3://my-bucket/a8d2-uploads/file1.jpg`) to distribute data across multiple S3 physical partition shards.

---

### Cost Optimization: Storage Tiering Lifecycle
| Tier | Storage Cost | Retrieval Fee | Retrieval Latency | Best For |
| :--- | :--- | :--- | :--- | :--- |
| **S3 Standard (Hot)** | 🔴 Highest ($23/TB/mo). | 🟢 Zero. | ⚡ Milliseconds (Instant). | Active user assets, profiles. |
| **S3 Infrequent Access** | 🟡 Medium ($12.5/TB/mo).| 🔴 Yes. | ⚡ Milliseconds (Instant). | Logs from 1 week ago, backups. |
| **S3 Glacier Flexible** | 🟢 Low ($3.6/TB/mo). | 🔴 Yes. | 🟡 1 to 5 Hours. | Historical audit archives. |
| **S3 Glacier Deep Archive**| 🟢 Lowest ($0.99/TB/mo).| 🔴 Yes. | 🔴 12 Hours. | Annual regulatory data backups. |

---

## 💥 3. Resiliency & Operations

### Operational Pitfalls & Mitigations
*   **The Incomplete Multipart Upload leak:**
    *   *Problem:* If a user closes the browser during a multipart upload, the uploaded chunks remain in S3 indefinitely, consuming disk space and incurring storage costs silently.
    *   *Mitigation:* Define an **S3 Lifecycle Policy** that automatically deletes incomplete multipart uploads older than 7 days.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Suggesting to mount S3 as a file system (using tools like s3fs) to host active database files (like Postgres/MySQL data). S3 does not support random writes inside files; modifying 1 byte inside a 10GB file forces S3 to re-upload the entire 10GB.
*   Not mentioning pre-signed URLs or prefix hashing optimizations when designing a media-heavy application (like YouTube or Instagram).

### Interview Tip (The "Strong Hire" Signal)
> *"For our video hosting platform, we design for high scalability using S3. We use pre-signed URLs to let clients upload media directly to S3, bypassing our application servers. To maximize write performance and avoid HTTP 503 partition limits, we prepend a random hexadecimal hash prefix to each object key. We automate lifecycle tiering to transition raw videos to S3 Infrequent Access after 30 days, and finally to Glacier Deep Archive after 90 days."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*

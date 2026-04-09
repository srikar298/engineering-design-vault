# 📦 14 - File & Object Storage (S3, HDFS, Blob)

## 📖 1. The Concept
Databases are great for structured data, but where do you store 5TB of user profile pictures or 100GB log files? You need specialized storage for **Unstructured Data**.

---

## 📊 2. The SDE-2 Trade-off Table: Storage Types

| Type | Examples | Best For | Pros/Cons |
| :--- | :--- | :--- | :--- |
| **Block Storage** | AWS EBS, SAN | Databases, OS Boot volumes. | High IOPS, low latency. **Expensive**, hard to scale globally. |
| **File Storage** | AWS EFS, NFS | Shared files, CMS content. | Hierarchical (Folders). **Scalability limits** compared to Object. |
| **Object Storage** | AWS S3, GCS | Images, Videos, Backups, Logs. | **Infinite Scale**, Cheap. **High Latency** (not for random writes). |

---

## 🏗️ 3. How Object Storage Works (The Interview Blueprint)
When you upload a file to S3:
1.  **Metadata**: Stored in a fast K-V store (e.g., File name, Size, Owner, URL).
2.  **Object Body**: Stored in a distributed file system (e.g., HDFS or proprietary clusters).
3.  **Versioning**: Every write creates a new version instead of updating in-place (Immutable).

---

## 🚀 4. The SDE-3 Edge: Content Addressing & Deduplication
If 1,000 users upload the exact same 5MB viral video, does your system store 5GB or 5MB?
*   **The Solution:** Use **Content-Addressable Storage**. Hash the file content (e.g., SHA-256). The hash becomes the Object ID. If the hash already exists, just point the new user to the existing block.
*   **Impact:** Massive cost savings and reduced network bandwidth for 10k+ concurrent users.

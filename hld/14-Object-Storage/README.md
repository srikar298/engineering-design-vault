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

An SDE-2 must explain the separation of control and data planes:
1.  **Control Plane (Metadata)**: Stored in a fast, indexed database (e.g., RowID -> LocalPath Mapping).
2.  **Data Plane (The Object)**: The raw bytes stored in a specialized cluster.

### Handling Large Files (10k+ Concurrent Users)
- **Multipart Uploads**: Split a 5GB video into 100MB chunks. Upload in parallel. If chunk #45 fails, you only retry chunk #45, not the whole 5GB.
- **Pre-signed URLs**: Instead of the client uploading/downloading *through* your app server (consuming your precious bandwidth), the app server generates a temporary URL that allows the client to talk **directly to S3**.

---

## ❄️ 4. Cost Optimization: Storage Tiers
A "Senior Signal" is mentioning that not all data is accessed equally.
- **S3 Standard (Hot)**: High cost, instant access. (User avatars).
- **S3 IA (Infrequent Access)**: Lower cost, slightly slower. (Logs from 1 week ago).
- **S3 Glacier (Archive)**: Near-zero cost, hours to retrieve. (Accounting backups from 2 years ago).

---

## 🚀 5. The SDE-3 Edge: Content Addressing & Deduplication
If 1,000 users upload the exact same 5MB viral video, don't store 5GB.
**The Solution:** Use **Content-Addressable Storage**. Hash the file content (e.g., SHA-256). The hash becomes the Object ID. If the hash already exists, just point the new user to the existing block.

**Senior Signal:** "By moving to **Pre-signed URLs**, we offloaded 90% of our ingress/egress traffic from our EC2 fleet to the storage provider, reducing our infrastructure costs by 40% and improving upload speeds for global users."

---

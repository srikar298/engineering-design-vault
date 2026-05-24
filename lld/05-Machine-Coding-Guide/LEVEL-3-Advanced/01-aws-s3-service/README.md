# 🪣 AWS S3 Service (Object Storage)

> **Patterns:** Composite · Strategy · Proxy · Builder

---

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Difficulty** | 🔴 Hard |
| **SDE-2 Mandatory** | ❌ No |
| **Patterns** | Composite, Strategy, Proxy |
| **Status** | Not Started |
| **Times Practiced** | 0 |
| **Last Practiced** | YYYY-MM-DD |
| **Next Review** | YYYY-MM-DD |

---


## 📋 The Prompt
Design the internals of an object storage service like AWS S3.

### 🛠️ Core Requirements
1.  **Hierarchy:** Support Buckets and Objects.
2.  **Versioning:** Support multiple versions of the same object.
3.  **Security:** Implement Access Control Lists (ACLs) per bucket or object.
4.  **Storage Classes:** Support different storage tiers (Standard, Intelligent-Tiering, Glacier) with different pricing and retrieval times.

### ⚙️ Constraints & Invariants
-   **Metadata vs Data:** Separate the metadata (size, owner, version) from the actual byte storage.
-   **Atomic Uploads:** An object is only available after the upload is fully complete.
-   **Scalability:** Design the class model to handle millions of objects per bucket.

---

## ✅ Self-Evaluation Checklist
- [ ] **Composite Pattern:** Did you use it to manage the Bucket/Folder/File hierarchy?
- [ ] **Strategy Pattern:** How do you handle different storage class behaviors?
- [ ] **Proxy Pattern:** Did you use a proxy for authentication and ACL checks?
- [ ] **Versioning Logic:** How is the "Latest Version" tracked efficiently?

---

## 📂 Practice
Go to the `practice/` folder and implement the `S3Engine` and `BucketManager`.

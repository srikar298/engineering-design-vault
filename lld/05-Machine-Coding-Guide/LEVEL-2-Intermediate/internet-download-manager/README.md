# 📥 Internet Download Manager (IDM)

> **Interview Time:** 45 Minutes | **Level:** SDE-2 (Concurrency Focus)

## 📋 The Prompt
Design a download manager that can download a large file by splitting it into multiple smaller chunks and downloading them in parallel.

### 🛠️ Core Requirements
1.  **Multi-part Download:** Split a file into `N` parts and download them using multiple threads.
2.  **Pause/Resume:** Ability to pause a download and resume from where it left off.
3.  **Progress Tracking:** Notify the UI of the total progress percentage across all threads.
4.  **Priority Queue:** Manage a queue of files where high-priority files are downloaded first.

### ⚙️ Constraints & Invariants
-   The system must handle network failures for individual chunks (Retries).
-   The progress calculation must be thread-safe.
-   Final assembly: Once all chunks are done, they must be merged into a single file.

---

## ✅ Self-Evaluation Checklist
- [ ] **Concurrency:** Did you use a `ThreadPool` or `ExecutorService`?
- [ ] **State Pattern:** Did you use the **State Pattern** for the download status (Idle, Downloading, Paused, Completed)?
- [ ] **Observer Pattern:** How do you notify the UI of progress? (Observer is the senior choice).
- [ ] **Atomic Variables:** Did you use `AtomicLong` for byte-count tracking to avoid race conditions?

---

## 📂 Practice
Go to the `practice/` folder and implement the core `DownloadTask` and `Orchestrator` logic.

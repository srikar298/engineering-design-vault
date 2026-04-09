# ⚙️ Task Scheduler LLD — Java Implementation

Ensure you are in the `JAVA` folder.

```bash
cd "03-Behavioral/12-LLD-Problems/02-task-scheduler/JAVA/"
javac iterator/*.java job/*.java engine/*.java Main.java
java Main
```

**Expected Console Output:**
```
==================================================
   Task Scheduler: Command + Template + Iterator  
==================================================

   [Queue] Added ReportJob with priority 1
   [Queue] Added BackupJob with priority 10
   [Queue] Added ReportJob with priority 5

--- ⚙️ Scheduler Booting Up... ---

[Job Engine] Starting Job: BackupJob (Priority 10)
   -> Custom Action: Checking if AWS S3 Bucket is reachable...
   -> Standard Action: Verifying system resources...
   -> Custom Task: Compressing Database and uploading to S3...
   -> Standard Action: Freeing memory buffers.

[Job Engine] Starting Job: ReportJob (Priority 5)
   -> Standard Action: Verifying system resources...
   -> Custom Task: Aggregating analytics and emailing PDF Report to CEO...
   -> Standard Action: Freeing memory buffers.

[Job Engine] Starting Job: ReportJob (Priority 1)
   -> Standard Action: Verifying system resources...
   -> Custom Task: Aggregating analytics and emailing PDF Report to CEO...
   -> Standard Action: Freeing memory buffers.

--- 🛑 Scheduler Finished: Queue is Empty ---
```

# ⚙️ LLD Case Study: Enterprise Task Scheduler

This module demonstrates how multiple Behavioral patterns act together to orchestrate backend workflows, such as cron-jobs or queue workers.

## 🧠 Architecture Overview

We are building a robust backend **Job Scheduler**. 

1. **Command Pattern:** Operations like `BackupDatabase` and `EmailReport` are extracted into standalone `Job` objects.
2. **Template Method Pattern:** The `BaseJob` implements a strict skeletal execution lifecycle: `preFlightCheck()` -> `runTask()` -> `cleanup()`. A developer creating a new Job only needs to write the `runTask()` logic, and the framework automatically guarantees that memory is cleaned up afterward.
3. **Iterator Pattern:** The `JobQueue` (a Priority Queue) issues a `PriorityJobIterator`. The `Scheduler` engine uses this iterator to loop over pending work smoothly without ever needing to know how the queue sorts its jobs under the hood.

---

## 💻 Tech Stack Highlights
* **Inversion of Control:** The Engine triggers `execute()` blindly on every Job. The Template Method handles the internal sequence.
* **Separation of Concerns:** The Iterator isolates traversal logic, while the Commands isolate business logic.

## ▶️ Execution
To run the Java implementation, go into the `JAVA` folder and see the specific `README.md`.

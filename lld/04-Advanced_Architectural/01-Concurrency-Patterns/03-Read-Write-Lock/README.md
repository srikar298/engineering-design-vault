# 📖 Read-Write Lock Pattern

## 📖 1. The Core Concept (The "Why")
The **Read-Write Lock** pattern is an optimization of standard synchronization locking. It fundamentally splits data access into two types of permissions: **Readers** and **Writers**.

### ⚠️ The Problem
In standard multithreading (`synchronized` in Java or a basic `Mutex`), only **one thread** can access a block of code at a time. 
If you have a banking database, and 1,000 customers all want to check their balance (a Read operation), a standard Mutex forces them to line up and check their balance one-by-one. This is incredibly inefficient because reading data doesn't change it! There is no danger in 1,000 people reading the exact same number simultaneously.

### ✅ The Solution
The Read-Write Lock splits the locks. 
*   **Read Lock:** Allows an infinite number of threads to acquire the lock simultaneously!
*   **Write Lock:** Completely exclusive. If someone wants to write, all incoming readers are blocked, they wait for current readers to finish, then they perform the write in total isolation.

---

## 💻 2. SDE-2+ Enterprise Implementation

In Enterprise Java, never try to build this using `wait()/notify()`. The standard is **`ReentrantReadWriteLock`**.

```java
private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

public String read() {
    lock.readLock().lock();    // 100 threads can pass this line simultaneously!
    try {
        return this.data;
    } finally {
        lock.readLock().unlock();
    }
}

public void write(String newData) {
    lock.writeLock().lock();   // Only 1 thread can pass. Blocks all readers!
    try {
        this.data = newData;
    } finally {
        lock.writeLock().unlock();
    }
}
```

### 🏗️ Why it matters for Scaling 
Almost every single High-Level Design (HLD) database in the world functions on this pattern.
*   **SQL Databases:** The "Shared Lock" (Read) and "Exclusive Lock" (Write) in PostgreSQL and MySQL are literal implementations of this pattern.
*   **Read-Heavy Systems:** If an application's traffic is 95% reads (like Twitter or YouTube), a Read-Write lock will perform 10,000x faster than a standard `synchronized` lock!

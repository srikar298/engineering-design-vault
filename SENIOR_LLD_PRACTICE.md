# Senior LLD Practice: Thread-Safe LRU Cache

## Problem Statement
Design and implement a **Thread-Safe Least Recently Used (LRU) Cache**. An LRU cache is a fixed-size cache that discards the least recently used items first when it reaches its capacity.

## Senior-Level Requirements
As an SDE-2/Senior candidate, your solution must address the following:

### 1. Concurrency & Thread-Safety
- The cache must be safe to use from multiple threads simultaneously.
- **Challenge**: How do you handle the race condition between checking if a key exists and updating its "recency"?
- **Optimization**: Can you avoid a "Global Lock" on the entire cache? (Hint: Consider `ReadWriteLock` or `ConcurrentHashMap` combined with fine-grained locking).

### 2. Time Complexity
- `get(key)`: Must be **O(1)**.
- `put(key, value)`: Must be **O(1)**.
- This typically requires a combination of a **HashMap** (for O(1) lookups) and a **Doubly Linked List** (for O(1) updates to recency).

### 3. Clean Architecture & SOLID
- Use the **Strategy Pattern** if you wanted to make the eviction policy swappable (e.g., LFU instead of LRU).
- Ensure **Interface Segregation**: Does the user need to know about the internal node structure? No.

### 4. Generics
- The cache should support any types for Keys and Values (e.g., `Cache<K, V>`).

---

## Implementation Skeleton (Java)

```java
public interface Cache<K, V> {
    V get(K key);
    void put(K key, V value);
    int size();
    void clear();
}

/**
 * Thread-Safe LRU Cache Implementation
 */
public class LRUCache<K, V> implements Cache<K, V> {
    private final int capacity;
    // TODO: Add internal data structures (HashMap, DoublyLinkedList)
    // TODO: Add synchronization mechanism (Locks)

    public LRUCache(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public V get(K key) {
        // TODO: Implement thread-safe get
        return null;
    }

    @Override
    public void put(K key, V value) {
        // TODO: Implement thread-safe put
    }

    // ... other methods
}
```

## Bonus "Senior" Questions
1. How would you handle **Cache Expiry** (TTL)?
2. How would you implement a **Distributed LRU Cache** (e.g., Redis)?
3. How does the **Double-Checked Locking** pattern apply here if we use a Singleton for the Cache Manager?

package addons.cache.decomposed;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * High-Performance, Generic, Thread-Safe LRU Cache.
 * Uses a {@link ConcurrentHashMap} for O(1) concurrent lookups and a custom {@link DoublyLinkedList}
 * for tracking recency of access. A {@link ReentrantReadWriteLock} protects the linked list mutations
 * while allowing lock-free lookups for cache misses.
 *
 * @param <K> type of keys
 * @param <V> type of values
 */
public class LRUCache<K, V> implements Cache<K, V> {
    private final int capacity;
    private final ConcurrentHashMap<K, Node<K, V>> map;
    private final DoublyLinkedList<K, V> list;
    
    // ReadWriteLock for thread-safe access control
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock readLock = rwl.readLock();
    private final Lock writeLock = rwl.writeLock();

    /**
     * Constructs a new LRUCache with the specified capacity limit.
     *
     * @param capacity the maximum number of items this cache can hold
     * @throws IllegalArgumentException if the capacity is non-positive
     */
    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.map = new ConcurrentHashMap<>(capacity);
        this.list = new DoublyLinkedList<>();
    }

    /**
     * Retrieves an item from the cache.
     * Provides extreme concurrency:
     * 1. Lock-free lookup for cache misses, resulting in zero lock contention.
     * 2. Double-checked locking under a write-lock to safely update node recency upon a hit.
     *
     * @param key the key to search for
     * @return the value associated with the key, or null if not found/evicted
     */
    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        // Fast lock-free lookup check
        Node<K, V> node = map.get(key);
        if (node == null) {
            return null; // Cache Miss: Complete lock-free bypass!
        }

        // Cache Hit: Acquire write-lock to safely adjust DLL pointers (structural update)
        writeLock.lock();
        try {
            // Double-checked locking: verify the node has not been evicted/removed during lock acquisition
            if (node.active && map.get(key) == node) {
                list.moveToHead(node);
                return node.value;
            }
        } finally {
            writeLock.unlock();
        }

        return null;
    }

    /**
     * Inserts or updates a cache entry.
     * Guarantees thread-safe updates to the mapping and recency list, handling evictions atomically.
     *
     * @param key   the key to insert or update
     * @param value the value to associate with the key
     */
    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }

        writeLock.lock();
        try {
            Node<K, V> existingNode = map.get(key);
            if (existingNode != null) {
                // Key exists: update value and move to MRU position
                existingNode.value = value;
                list.moveToHead(existingNode);
            } else {
                // Key does not exist: handle eviction if at capacity
                if (map.size() >= capacity) {
                    Node<K, V> evictedNode = list.removeTail();
                    if (evictedNode != null) {
                        map.remove(evictedNode.key);
                    }
                }
                
                // Add new entry
                Node<K, V> newNode = new Node<>(key, value);
                list.addToHead(newNode);
                map.put(key, newNode);
            }
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Returns the current size of the cache.
     * Uses a read lock to get a consistent view of the list size.
     */
    @Override
    public int size() {
        readLock.lock();
        try {
            return list.size();
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Clears all cache entries from the mapping and the recency list.
     */
    @Override
    public void clear() {
        writeLock.lock();
        try {
            map.clear();
            list.clear();
        } finally {
            writeLock.unlock();
        }
    }
}

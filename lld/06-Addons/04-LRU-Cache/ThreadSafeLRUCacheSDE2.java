package addons.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Map;

/**
 * <h1>04 - Thread-Safe LRU Cache (The "High-Performance" Data Store)</h1>
 * 
 * <b>Scenario:</b> You are building a <b>Session Cache</b> or a <b>Data Retrieval Cache</b>. 
 * Access to the database is slow, so you store results in memory. When the cache is 
 * full, the Least Recently Used (LRU) item is evicted.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Time Complexity:</b> O(1) for both <code>get</code> and <code>put</code>. 
 *    Achieved by combining a <code>HashMap</code> (for lookup) and a <code>Doubly Linked List</code> (for recency).
 * 2. <b>Fine-Grained Locking:</b> We use a <code>ConcurrentHashMap</code> for lookups 
 *    and a <code>ReentrantLock</code> only when modifying the Linked List. This 
 *    is more performant than a single "Global Lock".
 * 3. <b>Generics:</b> Uses <code><K, V></code> to ensure type safety.
 * 
 * <b>Edge Cases Handled:</b>
 * - <b>Cache Hit/Miss:</b> Correctly moves accessed items to the "MRU" (Most Recently Used) position.
 * - <b>Capacity Limit:</b> Evicts the "Tail" of the list when full.
 * - <b>Null Safety:</b> (Optional) Typically, caches reject null keys/values.
 */

interface Cache<K, V> {
    V get(K key);
    void put(K key, V value);
    int size();
}

class Node<K, V> {
    K key;
    V value;
    Node<K, V> prev, next;
    Node(K k, V v) { this.key = k; this.value = v; }
}

public class ThreadSafeLRUCacheSDE2<K, V> implements Cache<K, V> {

    private final int capacity;
    private final Map<K, Node<K, V>> map = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    // Sentinel Head/Tail nodes (Avoids null-checks in move/add logic)
    private final Node<K, V> head = new Node<>(null, null);
    private final Node<K, V> tail = new Node<>(null, null);

    public ThreadSafeLRUCacheSDE2(int capacity) {
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public V get(K key) {
        Node<K, V> node = map.get(key);
        if (node == null) return null;

        lock.lock();
        try {
            moveToHead(node);
        } finally {
            lock.unlock();
        }
        return node.value;
    }

    @Override
    public void put(K key, V value) {
        Node<K, V> node = map.get(key);
        
        lock.lock();
        try {
            if (node != null) {
                node.value = value;
                moveToHead(node);
            } else {
                if (map.size() >= capacity) {
                    Node<K, V> evicted = removeTail();
                    if (evicted != null) map.remove(evicted.key);
                }
                Node<K, V> newNode = new Node<>(key, value);
                addNode(newNode);
                map.put(key, newNode);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() { return map.size(); }

    // --- INTERNAL LIST OPERATIONS (Thread-Safety provided by the caller) ---

    private void addNode(Node<K, V> node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(Node<K, V> node) {
        removeNode(node);
        addNode(node);
    }

    private Node<K, V> removeTail() {
        if (tail.prev == head) return null;
        Node<K, V> res = tail.prev;
        removeNode(res);
        return res;
    }

    public static void main(String[] args) {
        ThreadSafeLRUCacheSDE2<String, Integer> cache = new ThreadSafeLRUCacheSDE2<>(2);

        cache.put("A", 1);
        cache.put("B", 2);
        System.out.println("Get A: " + cache.get("A")); // Moves A to MRU

        cache.put("C", 3); // Evicts B (since A was used recently)
        
        System.out.println("Get B (Should be null): " + cache.get("B"));
        System.out.println("Get C: " + cache.get("C"));
        System.out.println("Get A: " + cache.get("A"));
    }
}

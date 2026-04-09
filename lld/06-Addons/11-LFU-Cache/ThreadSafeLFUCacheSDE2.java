package addons.cache;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <h1>11 - Thread-Safe LFU Cache (The "Frequency" Champion)</h1>
 * 
 * <b>Scenario:</b> While LRU evicts based on "Recency", LFU evicts based on 
 * "Usage Count". This is better for systems where popular items remain 
 * popular for long periods (e.g., static configurations).
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Complexity:</b> O(1) for both <code>get</code> and <code>put</code>. 
 *    Requires 3 data structures:
 *    - A Map for actual data (K, V).
 *    - A Map for frequency counts (K, Count).
 *    - A Map of Sets to group Keys by frequency (Count, Set<K>).
 * 2. <b>Fine-Grained Locking:</b> Uses a <code>ReentrantLock</code> to protect 
 *    internal consistency during pointer and frequency updates.
 * 
 * <b>Edge Cases Handled:</b>
 * - <b>New Keys:</b> Start with Frequency = 1.
 * - <b>Existing Keys:</b> Increment frequency and move to the next frequency set.
 * - <b>Eviction:</b> Removes the oldest item from the **minimum frequency** set.
 */

public class ThreadSafeLFUCacheSDE2<K, V> {

    private final int capacity;
    private int minFrequency = -1;
    private final Map<K, V> vals = new HashMap<>();
    private final Map<K, Integer> counts = new HashMap<>();
    private final Map<Integer, LinkedHashSet<K>> lists = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    public ThreadSafeLFUCacheSDE2(int capacity) {
        this.capacity = capacity;
        lists.put(1, new LinkedHashSet<>());
    }

    public V get(K key) {
        lock.lock();
        try {
            if (!vals.containsKey(key)) return null;

            // 1. Get current frequency and increment
            int count = counts.get(key);
            counts.put(key, count + 1);

            // 2. Remove from current frequency list
            lists.get(count).remove(key);

            // 3. Update minFrequency if this was the last item in the minFreq list
            if (count == minFrequency && lists.get(count).isEmpty()) {
                minFrequency++;
            }

            // 4. Add to the next frequency list
            lists.computeIfAbsent(count + 1, k -> new LinkedHashSet<>()).add(key);

            return vals.get(key);
        } finally {
            lock.unlock();
        }
    }

    public void put(K key, V value) {
        if (capacity <= 0) return;

        lock.lock();
        try {
            if (vals.containsKey(key)) {
                vals.put(key, value);
                get(key); // Triggers frequency update
                return;
            }

            // 1. Handle Capacity (Evict minFreq item)
            if (vals.size() >= capacity) {
                K evit = lists.get(minFrequency).iterator().next();
                lists.get(minFrequency).remove(evit);
                vals.remove(evit);
                counts.remove(evit);
            }

            // 2. Insert new item
            vals.put(key, value);
            counts.put(key, 1);
            minFrequency = 1;
            lists.get(1).add(key);

        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ThreadSafeLFUCacheSDE2<String, String> cache = new ThreadSafeLFUCacheSDE2<>(2);

        cache.put("A", "DataA");
        cache.put("B", "DataB");
        System.out.println("Get A: " + cache.get("A")); // A freq = 2, B freq = 1

        cache.put("C", "DataC"); // Evicts B (minFreq = 1)
        
        System.out.println("Get B: " + cache.get("B")); // null
        System.out.println("Get C: " + cache.get("C")); // DataC
        System.out.println("Get A: " + cache.get("A")); // DataA
    }
}

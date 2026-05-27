package addons.cache.decomposed;

/**
 * Node representing an entry in the LRU Cache.
 * Contains volatile pointers and attributes to facilitate lock-free reads and double-checked synchronization.
 *
 * @param <K> the type of keys maintained by this node
 * @param <V> the type of mapped values
 */
public class Node<K, V> {
    public final K key;
    public volatile V value;
    public volatile Node<K, V> prev;
    public volatile Node<K, V> next;
    
    // Safety flag to guard against race conditions during eviction/removal.
    // Set to false when the node is detached from the DoublyLinkedList.
    public volatile boolean active;

    public Node(K key, V value) {
        this.key = key;
        this.value = value;
        this.active = true;
    }
}

package addons.cache.decomposed;

/**
 * Generic Cache Interface defining the contract for Cache operations.
 *
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of mapped values
 */
public interface Cache<K, V> {
    /**
     * Retrieve the value associated with the given key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value associated with the key, or null if it does not exist or has been evicted
     */
    V get(K key);

    /**
     * Associate the specified value with the specified key in this cache.
     * If the cache previously contained a mapping for the key, the old value is replaced.
     * If the cache is at capacity, the least recently used element is evicted.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     */
    void put(K key, V value);

    /**
     * Returns the number of key-value mappings in this cache.
     *
     * @return the number of key-value mappings in this cache
     */
    int size();

    /**
     * Removes all mappings from this cache.
     */
    void clear();
}

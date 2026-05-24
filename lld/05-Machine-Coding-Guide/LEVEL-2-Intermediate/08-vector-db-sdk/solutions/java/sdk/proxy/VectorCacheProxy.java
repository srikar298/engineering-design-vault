package sdk.proxy;

import sdk.IVectorDatabase;
import sdk.Vector;
import sdk.metrics.ISimilarityCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class VectorCacheProxy implements IVectorDatabase {
    private static class CacheEntry {
        final float[] queryVector;
        final List<Vector> results;
        long lastAccessTime;

        CacheEntry(float[] queryVector, List<Vector> results) {
            this.queryVector = queryVector;
            this.results = results;
            this.lastAccessTime = System.currentTimeMillis();
        }

        void touch() {
            this.lastAccessTime = System.currentTimeMillis();
        }
    }

    private final IVectorDatabase realDatabase;
    private final List<CacheEntry> cache = new ArrayList<>();
    private final int capacity;
    private final double similarityThreshold;
    private final ISimilarityCalculator calculator;
    private final ReentrantLock lock = new ReentrantLock();

    public VectorCacheProxy(IVectorDatabase realDatabase, ISimilarityCalculator calculator, int capacity, double similarityThreshold) {
        this.realDatabase = realDatabase;
        this.calculator = calculator;
        this.capacity = capacity;
        this.similarityThreshold = similarityThreshold;
    }

    @Override
    public void upsert(List<Vector> vectors) {
        lock.lock();
        try {
            // When writing new vectors, we invalidate the cache to prevent stale reads
            cache.clear();
            realDatabase.upsert(vectors);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Vector> query(float[] queryVector, int topK) {
        lock.lock();
        try {
            // Check semantic cache
            for (CacheEntry entry : cache) {
                double similarity = calculator.calculate(entry.queryVector, queryVector);
                if (similarity >= similarityThreshold) {
                    System.out.printf("[Cache Proxy] Cache HIT! Semantic Similarity = %.4f. Returning cached results.\n", similarity);
                    entry.touch();
                    return entry.results;
                }
            }

            // Cache miss: query database
            System.out.println("[Cache Proxy] Cache MISS. Querying underlying database...");
            List<Vector> results = realDatabase.query(queryVector, topK);

            // Add to cache
            if (cache.size() >= capacity) {
                evictLRU();
            }
            cache.add(new CacheEntry(queryVector, results));
            return results;
        } finally {
            lock.unlock();
        }
    }

    private void evictLRU() {
        if (cache.isEmpty()) return;
        CacheEntry oldest = cache.get(0);
        for (CacheEntry entry : cache) {
            if (entry.lastAccessTime < oldest.lastAccessTime) {
                oldest = entry;
            }
        }
        cache.remove(oldest);
        System.out.println("[Cache Proxy] Evicted oldest LRU cache entry.");
    }
}

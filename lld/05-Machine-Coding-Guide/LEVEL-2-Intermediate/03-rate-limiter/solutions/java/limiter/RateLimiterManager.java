package limiter;

import java.util.concurrent.ConcurrentHashMap;

public class RateLimiterManager {
    private static volatile RateLimiterManager instance = null;
    private final ConcurrentHashMap<String, IRateLimitAlgorithm> limiters = new ConcurrentHashMap<>();

    private RateLimiterManager() {}

    public static RateLimiterManager getInstance() {
        if (instance == null) {
            synchronized (RateLimiterManager.class) {
                if (instance == null) {
                    instance = new RateLimiterManager();
                }
            }
        }
        return instance;
    }

    public boolean isAllowed(String clientKey, long maxCapacity, long refillRatePerSecond) {
        IRateLimitAlgorithm limiter = limiters.computeIfAbsent(clientKey, 
            k -> new TokenBucketAlgorithm(maxCapacity, refillRatePerSecond));
        return limiter.isAllowed();
    }
}

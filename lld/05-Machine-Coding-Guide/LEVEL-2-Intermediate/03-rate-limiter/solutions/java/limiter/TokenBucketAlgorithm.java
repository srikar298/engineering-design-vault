package limiter;

import java.util.concurrent.atomic.AtomicReference;

public class TokenBucketAlgorithm implements IRateLimitAlgorithm {
    private final long maxCapacity;
    private final long refillRatePerSecond;

    private static class BucketState {
        final long tokens;
        final long lastRefillTime;

        BucketState(long tokens, long lastRefillTime) {
            this.tokens = tokens;
            this.lastRefillTime = lastRefillTime;
        }
    }

    private final AtomicReference<BucketState> state;

    public TokenBucketAlgorithm(long maxCapacity, long refillRatePerSecond) {
        this.maxCapacity = maxCapacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.state = new AtomicReference<>(new BucketState(maxCapacity, System.currentTimeMillis()));
    }

    @Override
    public boolean isAllowed() {
        while (true) {
            long now = System.currentTimeMillis();
            BucketState current = state.get();

            long elapsed = now - current.lastRefillTime;
            // Guard against system clock skew moving backwards
            if (elapsed < 0) {
                elapsed = 0;
            }

            long tokensToAdd = (elapsed * refillRatePerSecond) / 1000;

            long newTokens = current.tokens;
            long newRefillTime = current.lastRefillTime;

            if (tokensToAdd > 0) {
                newTokens = Math.min(maxCapacity, current.tokens + tokensToAdd);
                newRefillTime = now;
            }

            if (newTokens < 1) {
                return false; // Rate limited
            }

            BucketState updated = new BucketState(newTokens - 1, newRefillTime);
            if (state.compareAndSet(current, updated)) {
                return true;
            }
        }
    }
}

package limiter;

public interface IRateLimitAlgorithm {
    boolean isAllowed();
}

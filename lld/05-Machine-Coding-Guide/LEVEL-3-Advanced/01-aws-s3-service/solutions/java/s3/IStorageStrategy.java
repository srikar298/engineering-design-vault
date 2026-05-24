package s3;

public interface IStorageStrategy {
    double calculateCost(long sizeBytes);
    long getRetrievalDelayMs();
}

class StandardStorageStrategy implements IStorageStrategy {
    @Override
    public double calculateCost(long sizeBytes) {
        // Standard rate: $0.023 per GB
        double sizeInGb = (double) sizeBytes / (1024 * 1024 * 1024);
        return sizeInGb * 0.023;
    }

    @Override
    public long getRetrievalDelayMs() {
        return 0; // Immediate access
    }
}

class GlacierStorageStrategy implements IStorageStrategy {
    @Override
    public double calculateCost(long sizeBytes) {
        // Glacier rate: $0.004 per GB
        double sizeInGb = (double) sizeBytes / (1024 * 1024 * 1024);
        return sizeInGb * 0.004;
    }

    @Override
    public long getRetrievalDelayMs() {
        return 400; // Simulated delay (400ms instead of 3-5 hours for demo speed)
    }
}

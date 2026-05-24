package parking;

public interface IPricingStrategy {
    double calculateFee(long durationMs);
}

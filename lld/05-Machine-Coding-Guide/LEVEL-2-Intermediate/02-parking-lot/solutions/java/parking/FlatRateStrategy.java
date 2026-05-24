package parking;

public class FlatRateStrategy implements IPricingStrategy {
    private final double ratePerHour;

    public FlatRateStrategy(double ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    @Override
    public double calculateFee(long durationMs) {
        long hours = (long) Math.ceil(durationMs / 3600000.0);
        if (hours <= 0) hours = 1;
        return hours * ratePerHour;
    }
}

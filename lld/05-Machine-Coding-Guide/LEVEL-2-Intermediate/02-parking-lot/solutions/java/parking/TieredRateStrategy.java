package parking;

public class TieredRateStrategy implements IPricingStrategy {
    @Override
    public double calculateFee(long durationMs) {
        long hours = (long) Math.ceil(durationMs / 3600000.0);
        if (hours <= 0) hours = 1;

        double fee = 0;
        if (hours >= 1) {
            fee += 4.0; // first hour
        }
        if (hours >= 2) {
            fee += 6.0; // second hour
        }
        if (hours > 2) {
            fee += (hours - 2) * 10.0; // subsequent hours
        }
        return fee;
    }
}

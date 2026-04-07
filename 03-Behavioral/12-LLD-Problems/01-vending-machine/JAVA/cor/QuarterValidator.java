package cor;

public class QuarterValidator extends CoinValidator {
    @Override
    public boolean validate(double coinWeight) {
        if (coinWeight == 0.25) { // Assuming weight simulates value for this example
            System.out.println("   [Hardware] Recognized Quarter ($0.25)");
            return true;
        }
        return checkNext(coinWeight);
    }
}

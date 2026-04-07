package cor;

public class DimeValidator extends CoinValidator {
    @Override
    public boolean validate(double coinWeight) {
        if (coinWeight == 0.10) { 
            System.out.println("   [Hardware] Recognized Dime ($0.10)");
            return true;
        }
        return checkNext(coinWeight);
    }
}

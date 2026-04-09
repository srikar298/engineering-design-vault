package cor;

/**
 * <h1>Chain of Responsibility: Coin Validator</h1>
 * 
 * <p>Simulates the hardware slot of a Vending Machine.
 * Coins drop through a physical mechanism that filters out slugs or invalid currency.
 */
public abstract class CoinValidator {
    private CoinValidator next;

    public CoinValidator setNext(CoinValidator next) {
        this.next = next;
        return next;
    }

    public abstract boolean validate(double coinWeight);

    protected boolean checkNext(double coinWeight) {
        if (next == null) {
            return false; // Escaped the chain, meaning NO validator recognized it. Fake coin!
        }
        return next.validate(coinWeight);
    }
}

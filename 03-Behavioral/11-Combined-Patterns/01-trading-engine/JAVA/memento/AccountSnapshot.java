package memento;

/**
 * <h1>Memento: The Snapshot</h1>
 * 
 * <p>Immutable snapshot of the trader's account balance and portfolio value.
 */
public class AccountSnapshot {
    private final double cashBalance;

    public AccountSnapshot(double cashBalance) {
        this.cashBalance = cashBalance;
    }

    public double getCashBalance() {
        return cashBalance;
    }
}

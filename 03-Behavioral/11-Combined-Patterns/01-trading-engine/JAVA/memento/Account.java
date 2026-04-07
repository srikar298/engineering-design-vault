package memento;

/**
 * <h1>Originator: The Trader's Account</h1>
 */
public class Account {
    private double cashBalance;

    public Account(double initialBalance) {
        this.cashBalance = initialBalance;
    }

    public void debit(double amount) { this.cashBalance -= amount; }
    public void credit(double amount) { this.cashBalance += amount; }
    
    public double getBalance() { return cashBalance; }

    // --- Memento Methods ---
    
    public AccountSnapshot save() {
        return new AccountSnapshot(this.cashBalance);
    }

    public void restore(AccountSnapshot snapshot) {
        this.cashBalance = snapshot.getCashBalance();
        System.out.println("   [System] Account Balance rolled back to: $" + this.cashBalance);
    }
}

package encapsulation;

import java.lang.reflect.Field;

/**
 * <h1>Encapsulation: The "Invariant" Protector (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> A Bank Account. 
 * Invariant: The balance must NEVER be negative.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Validation:</b> Setters/Methods must reject invalid states.
 * 2. <b>Reflection Trap:</b> Java's Reflection API can bypass <code>private</code>. 
 *    Senior engineers know that while you can't block reflection easily, 
 *    you should design for "Atomic State" to minimize damage.
 * 3. <b>Tell Don't Ask:</b> Instead of <code>if(acc.getBalance() > 100) acc.withdraw(100)</code>, 
 *    we use <code>acc.withdraw(100)</code> and let the object decide.
 */

class BankAccount {
    // --- [INTERVIEW_MVP] (Data Hiding) ---
    private double balance;

    public BankAccount(double initialBalance) {
        if (initialBalance < 0) throw new IllegalArgumentException();
        this.balance = initialBalance;
    }

    /**
     * [INTERVIEW_MVP]: Business Logic instead of raw setters.
     */
    public void withdraw(double amount) {
        // Enforcing the Invariant
        if (amount > balance) {
            throw new IllegalStateException("Insufficient funds!");
        }
        this.balance -= amount;
        System.out.println("Withdrew: " + amount + " | New Balance: " + balance);
    }

    public double getBalance() { return balance; }
}

public class EncapsulationSDE2 {
    public static void main(String[] args) throws Exception {
        BankAccount account = new BankAccount(1000);

        // [INTERVIEW_MVP]: Standard usage
        account.withdraw(500);

        // --- [PRODUCTION_ENHANCEMENT] (Reflection Awareness) ---
        System.out.println("\n[HACK] Attempting to bypass encapsulation via Reflection...");
        Field field = BankAccount.class.getDeclaredField("balance");
        field.setAccessible(true);
        field.set(account, -99999.0); // Bypassing all logic!

        System.out.println("Hacked Balance: " + account.getBalance());
        System.out.println("⚠️ Senior Insight: Access modifiers are not a security boundary against " +
                           "internal code, but a maintenance boundary for APIs.");
    }
}

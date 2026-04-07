package liskov_substitution;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>LSP: The "Behavioral" Principle (SDE-2+ Level)</h1>
 * 
 * Barbara Liskov's actual definition: 
 * "Objects in a program should be replaceable with instances of their 
 * subtypes without altering the correctness of that program."
 * 
 * Key Rules for SDE-2+:
 * 1. Don't STRENGTHEN Pre-conditions (don't demand MORE than the parent).
 * 2. Don't WEAKEN Post-conditions (don't promise LESS than the parent).
 * 3. Don't change the "Invariant" (rules that must always be true).
 */

abstract class BankAccount {
    protected double balance;

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit must be positive");
        this.balance += amount;
    }

    /**
     * Parent Contract: Can withdraw ANY amount up to current balance.
     */
    public abstract void withdraw(double amount);

    public double getBalance() { return balance; }
}

class CheckingAccount extends BankAccount {
    @Override
    public void withdraw(double amount) {
        if (amount > balance) throw new IllegalStateException("Insufficient funds");
        this.balance -= amount;
        System.out.println("Checking: Withdrew $" + amount);
    }
}

/**
 * ❌ VIOLATION: SavingsAccount strengthens the pre-condition.
 * It adds a rule: "You cannot withdraw more than $1000 at a time."
 * 
 * If a caller has a BankAccount reference, they expect to withdraw $5000 
 * if the balance is $10,000. SavingsAccount BREAKS this expectation.
 */
class SavingsAccountViolation extends BankAccount {
    @Override
    public void withdraw(double amount) {
        // ❌ STRENGTHENED PRE-CONDITION: Demand more than the parent
        if (amount > 1000) {
            throw new IllegalArgumentException("Savings limit: $1000 max withdrawal");
        }
        if (amount > balance) throw new IllegalStateException("Insufficient funds");
        this.balance -= amount;
        System.out.println("Savings (Violation): Withdrew $" + amount);
    }
}

/**
 * ✅ REFACTORED: Use Composition or a more specific hierarchy.
 * Not every account is a "WithdrawableAccount" in the same way.
 */
interface Withdrawable {
    void withdraw(double amount);
}

public class LSPPragmaticSDE2 {
    public static void main(String[] args) {
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new CheckingAccount());
        accounts.add(new SavingsAccountViolation()); // LSP Violation added

        for (BankAccount acc : accounts) {
            acc.deposit(5000);
            try {
                // The caller expects this to work for ANY BankAccount with $5000 balance
                acc.withdraw(2000); 
                System.out.println("✅ Withdrawal successful for: " + acc.getClass().getSimpleName());
            } catch (Exception e) {
                // ❌ SavingsAccountViolation fails here, breaking the program's correctness
                System.err.println("❌ LSP VIOLATION in " + acc.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }
}

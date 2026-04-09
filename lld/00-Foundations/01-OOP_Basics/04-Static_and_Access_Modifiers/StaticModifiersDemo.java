/**
 * Demonstrates the monumental difference between Instance state and Static state,
 * as well as the importance of Access Modifiers.
 */
public class StaticModifiersDemo {

    public static void main(String[] args) {
        System.out.println("--- 1. Instance State vs Static State ---");
        BankAccount a = new BankAccount("Alice");
        BankAccount b = new BankAccount("Bob");

        // They have isolated instance state
        a.deposit(100);
        b.deposit(50);
        System.out.println("Alice's Balance: $" + a.getBalance()); // 100
        System.out.println("Bob's Balance: $" + b.getBalance());   // 50

        // But they share the STATIC state (Total accounts created)
        System.out.println("Total Accounts in System: " + BankAccount.getTotalAccounts()); // 2

        System.out.println("\n--- 2. The Danger of Public Static Variables ---");
        // Imagine if 'bankName' was public static instead of private static.
        // Any class anywhere could do: BankAccount.bankName = "Hacked Bank";
        // This is why static variables must be final, or strictly private.
        System.out.println("Bank Name: " + BankAccount.getBankName());
    }
}

class BankAccount {
    // --- STATIC (Class Level) ---
    // Lives in Metaspace. Shared across ALL instances.
    private static int totalAccounts = 0; // Mutable, guarded by private
    private static final String bankName = "FAANG Secure Bank"; // Immutable Constant

    // --- INSTANCE (Object Level) ---
    // Lives on the Heap. Unique to each object.
    private final String owner;
    private double balance; // Guarded by private

    /**
     * Constructor acts as the gatekeeper.
     */
    public BankAccount(String owner) {
        this.owner = owner;
        this.balance = 0;
        
        // Modifying the shared Global state safely
        totalAccounts++; 
    }

    // --- BEHAVIOR with Access Modifiers ---
    
    // PUBLIC: Anyone can call this to safely interact with the object.
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            logTransaction("Deposit of $" + amount);
        }
    }

    // PRIVATE: Internal helper method. The outside world doesn't need to know this exists.
    private void logTransaction(String message) {
        System.out.println("[LOG - " + bankName + "] " + owner + ": " + message);
    }

    // GETTERS
    public double getBalance() { return balance; }
    public static int getTotalAccounts() { return totalAccounts; }
    public static String getBankName() { return bankName; }
}

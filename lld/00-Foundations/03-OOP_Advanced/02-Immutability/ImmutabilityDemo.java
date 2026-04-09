import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================================
 * 🔒 IMMUTABILITY MASTERY: The 5-Rule Recipe & The Reference Trap
 * ============================================================================
 */

// ----------------------------------------------------------------------------
// ✅ A CORRECTLY IMMUTABLE CLASS: MoneyAmount
// Rule 1: Class is final
// ----------------------------------------------------------------------------
final class MoneyAmount {

    // Rule 2: All fields are private AND final
    private final double amount;
    private final String currency;
    private final List<String> transactionTags; // Mutable object — needs deep copy!

    // Rule 3: Constructor does ALL the validation and deep-copying
    public MoneyAmount(double amount, String currency, List<String> tags) {
        if (amount < 0) throw new IllegalArgumentException("Amount cannot be negative.");
        this.amount = amount;
        this.currency = currency;
        // Rule 4: Deep copy mutable inputs — don't retain the caller's reference!
        this.transactionTags = new ArrayList<>(tags);
    }

    // Rule 4 (Setters): NO setters exist. Operations return NEW objects.
    public MoneyAmount add(double valueToAdd) {
        return new MoneyAmount(this.amount + valueToAdd, this.currency, this.transactionTags);
    }

    public MoneyAmount subtract(double valueToSubtract) {
        return new MoneyAmount(this.amount - valueToSubtract, this.currency, this.transactionTags);
    }

    // Rule 5: Defensive copies in getters for mutable fields
    public List<String> getTransactionTags() {
        return Collections.unmodifiableList(this.transactionTags);
    }

    public double getAmount()   { return amount; }
    public String getCurrency() { return currency; }

    @Override
    public String toString() {
        return String.format("%s %.2f (tags: %s)", currency, amount, transactionTags);
    }
}


// ----------------------------------------------------------------------------
// 🚀 EXECUTION
// ----------------------------------------------------------------------------
public class ImmutabilityDemo {
    public static void main(String[] args) {

        System.out.println("=== 🔒 Immutability & The Reference Trap ===\n");

        // 1. Prove that the caller's original list cannot corrupt the MoneyAmount
        List<String> myTags = new ArrayList<>();
        myTags.add("initial-payment");

        MoneyAmount wallet = new MoneyAmount(500.0, "USD", myTags);
        System.out.println("Wallet created: " + wallet);

        // The caller NOW mutates their original list
        myTags.add("HACKER_TAG");
        System.out.println("Caller list mutated. Wallet after: " + wallet);
        // ✅ Output: tags are unchanged because we deep-copied in the constructor

        System.out.println();

        // 2. Prove that operations return NEW objects (immutability in action)
        MoneyAmount original = new MoneyAmount(1000.0, "USD", List.of("salary"));
        MoneyAmount afterFee  = original.subtract(50.0);
        MoneyAmount afterTax  = afterFee.subtract(100.0);

        System.out.println("Original: " + original);  // Still 1000.0 — unchanged
        System.out.println("AfterFee: " + afterFee);   // 950.0
        System.out.println("AfterTax: " + afterTax);   // 850.0
        System.out.println("Original still intact: " + original.getAmount()); // 1000.0 ✅

        System.out.println();

        // 3. Prove the getter cannot mutate the internal list
        System.out.println("Attempting to breach the getter...");
        try {
            wallet.getTransactionTags().add("BREACH"); // Should throw
        } catch (UnsupportedOperationException e) {
            System.out.println("✅ Getter breach blocked by unmodifiableList!");
        }
    }
}

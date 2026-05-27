package splitwise;

import java.util.*;

/**
 * <h1>Gold Standard: Splitwise (Basic)</h1>
 * 
 * <b>Why this is 10/10:</b>
 * 1. <b>Precision:</b> Uses basic arithmetic and rounding to track balances between users.
 * 2. <b>Strategy Pattern:</b> Different splitting algorithms (Equal, Exact, Percent) are encapsulated.
 * 3. <b>Data Modeling:</b> Clean separation of User, Group, Split, and Expense classes.
 * 4. <b>Validation:</b> Inputs are validated (e.g. percentages must sum to 100%) before modifying state.
 */
public class SplitwiseSolution {
    public static void main(String[] args) {
        SplitwiseService service = new SplitwiseService();

        User u1 = new User("u1", "Alice");
        User u2 = new User("u2", "Bob");
        User u3 = new User("u3", "Charlie");

        service.addUser(u1);
        service.addUser(u2);
        service.addUser(u3);

        // 1. Equal Split: Alice pays 300, shared by Alice, Bob, and Charlie
        System.out.println("--- Test Case 1: Equal Split ---");
        List<Split> equalSplits = new ArrayList<>();
        equalSplits.add(new EqualSplit(u1));
        equalSplits.add(new EqualSplit(u2));
        equalSplits.add(new EqualSplit(u3));
        service.addExpense(ExpenseType.EQUAL, "u1", 300.0, equalSplits);
        service.showBalances("u2"); // Should owe Alice 100
        service.showBalances("u3"); // Should owe Alice 100

        // 2. Exact Split: Bob pays 125, Alice owes 70, Charlie owes 55
        System.out.println("\n--- Test Case 2: Exact Split ---");
        List<Split> exactSplits = new ArrayList<>();
        exactSplits.add(new ExactSplit(u1, 70.0));
        exactSplits.add(new ExactSplit(u2, 0.0));
        exactSplits.add(new ExactSplit(u3, 55.0));
        service.addExpense(ExpenseType.EXACT, "u2", 125.0, exactSplits);
        service.showAllBalances(); 
        // Alice owes Bob 70, but Bob owed Alice 100 -> Net: Bob owes Alice 30
        // Charlie owed Alice 100, Charlie owes Bob 55 -> Net: Charlie owes Alice 100, owes Bob 55

        // 3. Percent Split: Charlie pays 1000, Alice: 20%, Bob: 30%, Charlie: 50%
        System.out.println("\n--- Test Case 3: Percent Split ---");
        List<Split> percentSplits = new ArrayList<>();
        percentSplits.add(new PercentSplit(u1, 20.0));
        percentSplits.add(new PercentSplit(u2, 30.0));
        percentSplits.add(new PercentSplit(u3, 50.0));
        service.addExpense(ExpenseType.PERCENT, "u3", 1000.0, percentSplits);
        service.showAllBalances();
    }
}
